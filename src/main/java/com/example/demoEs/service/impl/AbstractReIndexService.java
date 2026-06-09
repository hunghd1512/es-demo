package com.example.demoEs.service.impl;

import com.example.demoEs.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Abstract base class định nghĩa template method cho reindex.
 *
 * <p>Template Method Pattern: base class định nghĩa skeleton của thuật toán reindex, các bước
 * đặc thù được delegate sang subclass qua abstract methods.
 *
 * <p><strong>Flow chung:</strong>
 *
 * <ol>
 *   <li>Xác định index/source và target
 *   <li>Fetch data từ nguồn (JPA hoặc ES)
 *   <li>Transform từng document song song qua Executor (bất đồng bộ)
 *   <li>Bulk save vào target index
 *   <li>Refresh target index
 *   <li>Log tiến độ + xử lý failed documents
 * </ol>
 *
 * <p><strong>Thêm loại document mới:</strong> Chỉ cần kế thừa và override 3 methods:
 *
 * <pre>{@code
 * @Service
 * public class FooReIndexServiceImpl extends AbstractReIndexService<FooES, FooES, String> {
 *     @Override protected String getSourceIndex() { return "foo_index"; }
 *     @Override protected String getTargetIndex() { return "foo_index_v2"; }
 *     @Override protected FooES transform(FooES source) { return mapper.toES(source); }
 *     @Override protected Class<FooES> getSourceClass() { return FooES.class; }
 * }
 * }</pre>
 *
 * @param <S> Source entity type (đọc từ ES index)
 * @param <T> Target entity type (ghi vào index đích)
 * @param <ID> Primary key type
 */
@Slf4j
public abstract class AbstractReIndexService<S, T, ID> {

  protected final ElasticsearchOperations elasticsearchOperations;

  /** Batch size mặc định cho phân trang reindex. */
  protected static final int DEFAULT_BATCH_SIZE = 100;

  /** Số luồng mặc định cho transform song song. */
  protected static final int DEFAULT_PARALLELISM = 8;

  /** Timeout mặc định (phút) chờ transform hoàn tất. */
  protected static final int DEFAULT_TIMEOUT_MINUTES = 30;

  protected AbstractReIndexService(ElasticsearchOperations elasticsearchOperations) {
    this.elasticsearchOperations = elasticsearchOperations;
  }

  // ======================== TEMPLATE METHOD (final — subclass không override) ========================

  /**
   * Template method — skeleton của thuật toán reindex. KHÔNG override method này.
   *
   * <p>Transform được thực hiện song song qua {@link ExecutorService} (bất đồng bộ, không dùng
   * @Async). Mỗi document được transform trong một task riêng, sau đó bulk save theo batch.
   *
   * @param sourceIds danh sách IDs cần reindex (null = toàn bộ)
   */
  public final void reIndex(List<ID> sourceIds) {
    String sourceIndex = getSourceIndex();
    String targetIndex = getTargetIndex();
    int batchSize = getBatchSize();
    int parallelism = getParallelism();

    log.info("Bắt đầu reindex: {} → {} (batchSize={}, parallelism={})",
        sourceIndex, targetIndex, batchSize, parallelism);

    if (!indexExists(sourceIndex)) {
      throw new BusinessException("Index nguồn không tồn tại: " + sourceIndex);
    }

    List<S> sourceDocuments = fetchFromSource(sourceIds, IndexCoordinates.of(sourceIndex));
    if (sourceDocuments.isEmpty()) {
      log.info("Không có document nào để reindex");
      return;
    }

    log.info("Tìm thấy {} document(s) từ '{}'", sourceDocuments.size(), sourceIndex);

    ExecutorService executor = buildExecutor(parallelism);
    try {
      int result = executeTransformInParallel(executor, sourceDocuments, batchSize, targetIndex);
      refreshIndex(IndexCoordinates.of(targetIndex));
      onReindexComplete(result, 0);
      log.info("Hoàn tất reindex: processed={}", result);
    } finally {
      shutdownExecutor(executor);
    }
  }

  // ======================== EXECUTOR (bất đồng bộ) ========================

  /**
   * Xây dựng ExecutorService cho transform song song.
   *
   * <p>Subclass có thể override {@link #getParallelism()} để thay đổi số luồng, hoặc override
   * method này để cung cấp Executor tùy chỉnh (ví dụ: ThreadPoolTaskExecutor với queue).
   *
   * @param parallelism số luồng song song
   * @return ExecutorService đã khởi tạo
   */
  protected ExecutorService buildExecutor(int parallelism) {
    return new ThreadPoolExecutor(
        parallelism,
        parallelism,
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(1024),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  /**
   * Tắt ExecutorService đã dùng.
   *
   * @param executor ExecutorService cần shutdown
   */
  protected void shutdownExecutor(ExecutorService executor) {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
        executor.shutdownNow();
        log.warn("Executor không tắt trong {} phút, force shutdown", DEFAULT_TIMEOUT_MINUTES);
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Thực hiện transform song song qua ExecutorService và bulk save theo batch.
   *
   * <p>Flow:
   *
   * <ol>
   *   <li>Submit transform tasks cho tất cả documents vào executor (song song)
   *   <li>Chờ kết quả với timeout
   *   <li>Gom kết quả thành batch, bulk save vào ES
   *   <li>Đếm số thành công / thất bại
   * </ol>
   *
   * @param executor ExecutorService
   * @param sourceDocuments danh sách documents cần transform
   * @param batchSize kích thước mỗi bulk save batch
   * @param targetIndex tên index đích
   * @return số document đã xử lý thành công
   */
  private int executeTransformInParallel(
      ExecutorService executor,
      List<S> sourceDocuments,
      int batchSize,
      String targetIndex) {

    int total = sourceDocuments.size();
    List<Future<T>> futures = new ArrayList<>(total);

    // 1. Submit tất cả transform tasks (song song)
    for (S source : sourceDocuments) {
      Future<T> future = executor.submit(() -> transform(source));
      futures.add(future);
    }

    // 2. Thu thập kết quả + bulk save theo batch
    int processed = 0;
    int failed = 0;
    List<T> batch = new ArrayList<>(batchSize);

    for (int i = 0; i < futures.size(); i++) {
      Future<T> future = futures.get(i);
      try {
        T result = future.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (result != null) {
          batch.add(result);
        }
        processed++;
      } catch (ExecutionException e) {
        log.error("Transform thất bại cho document[{}]: {}",
            i, e.getCause().getMessage());
        failed++;
      } catch (TimeoutException e) {
        log.error("Transform timeout cho document[{}] sau {} phút", i, DEFAULT_TIMEOUT_MINUTES);
        failed++;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Transform bị interrupt cho document[{}]", i);
        failed++;
        break;
      }

      // Bulk save khi batch đầy
      if (batch.size() >= batchSize) {
        bulkSave(new ArrayList<>(batch), IndexCoordinates.of(targetIndex));
        batch.clear();
        log.info("Tiến độ: {}/{} (failed: {})", processed, total, failed);
      }
    }

    // 3. Bulk save batch cuối cùng
    if (!batch.isEmpty()) {
      bulkSave(batch, IndexCoordinates.of(targetIndex));
      log.info("Tiến độ: {}/{} (failed: {})", processed, total, failed);
    }

    if (failed > 0) {
      onReindexComplete(processed - failed, failed);
    }

    return processed - failed;
  }

  // ======================== ABSTRACT METHODS (subclass bắt buộc override) ========================

  /** Tên index/document source. */
  protected abstract String getSourceIndex();

  /** Tên index/document target. */
  protected abstract String getTargetIndex();

  /**
   * Transform một document từ nguồn sang đích.
   *
   * @param source document nguồn
   * @return document đã transform
   */
  protected abstract T transform(S source);

  /** Class type của source entity (dùng cho ES get). */
  protected abstract Class<S> getSourceClass();

  // ======================== HOOK METHODS (subclass override nếu cần) ========================

  /** Batch size. Mặc định 100. */
  protected int getBatchSize() {
    return DEFAULT_BATCH_SIZE;
  }

  /**
   * Số luồng song song cho transform. Mặc định 8.
   *
   * <p>Tăng giá trị này nếu CPU mạnh và transform là CPU-bound. Giảm nếu transform gọi I/O nặng.
   */
  protected int getParallelism() {
    return DEFAULT_PARALLELISM;
  }

  /** Callback khi reindex hoàn tất (có thể override để alert/retry). */
  protected void onReindexComplete(int processed, int failed) {}

  // ======================== HELPER METHODS ========================

  private List<S> fetchFromSource(List<ID> ids, IndexCoordinates sourceCoords) {
    CrudRepository<S, ID> jpaRepo = getJpaRepository();
    if (jpaRepo != null) {
      return fetchFromJpa(ids, jpaRepo);
    }
    return fetchFromElasticsearch(ids, sourceCoords);
  }

  private List<S> fetchFromJpa(List<ID> ids, CrudRepository<S, ID> repo) {
    if (ids == null || ids.isEmpty()) {
      return StreamSupport.stream(repo.findAll().spliterator(), false)
          .collect(Collectors.toList());
    }
    return StreamSupport.stream(repo.findAllById(ids).spliterator(), false)
        .collect(Collectors.toList());
  }

  private List<S> fetchFromElasticsearch(List<ID> ids, IndexCoordinates sourceCoords) {
    Class<S> clazz = getSourceClass();
    if (ids != null && !ids.isEmpty()) {
      List<S> results = new ArrayList<>();
      for (ID id : ids) {
        try {
          S doc = elasticsearchOperations.get((String) id, clazz, sourceCoords);
          if (doc != null) {
            results.add(doc);
          }
        } catch (Exception e) {
          log.warn("Không tìm thấy document '{}': {}", id, e.getMessage());
        }
      }
      return results;
    }
    NativeQuery query = new NativeQueryBuilder()
        .withPageable(PageRequest.of(0, getBatchSize()))
        .build();
    return elasticsearchOperations.search(query, clazz, sourceCoords)
        .getSearchHits().stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList());
  }

  /** Hook: override nếu dùng JPA thay vì ES. Mặc định null (dùng ES). */
  protected CrudRepository<S, ID> getJpaRepository() {
    return null;
  }

  private void bulkSave(List<T> documents, IndexCoordinates targetCoords) {
    if (documents == null || documents.isEmpty()) {
      return;
    }
    elasticsearchOperations.save(documents, targetCoords);
  }

  private void refreshIndex(IndexCoordinates coords) {
    try {
      elasticsearchOperations.indexOps(coords).refresh();
    } catch (Exception e) {
      log.warn("Refresh index '{}' thất bại: {}", coords.getIndexNames(), e.getMessage());
    }
  }

  private boolean indexExists(String indexName) {
    try {
      return elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists();
    } catch (Exception e) {
      return false;
    }
  }
}
