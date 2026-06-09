package com.example.demoEs.service.impl;

import com.example.demoEs.constant.ConfigConstant;
import com.example.demoEs.constant.QueryType;
import com.example.demoEs.controller.dto.PageResponse;
import com.example.demoEs.controller.dto.SearchPreventResponse;
import com.example.demoEs.es.document.PreventES;
import com.example.demoEs.es.dto.PreventSearchResultDTO;
import com.example.demoEs.search.PreventSearchQueryBuilder;
import com.example.demoEs.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của {@link SearchService} xử lý tra cứu thông tin ngăn chặn trên Elasticsearch.
 *
 * <p>Service này thực hiện tra cứu với các đặc điểm:
 *
 * <ul>
 *   <li>Tìm kiếm 2-tier: accent-sensitive (exact) + accent-insensitive (Vietnamese)
 *   <li>Hỗ trợ AND/OR query mode
 *   <li>Hỗ trợ wildcard search
 *   <li>Phân trang với kiểm soát kích thước trang
 *   <li>Sort theo relevance score và thời gian
 * </ul>
 *
 * <p><strong>Field mapping:</strong> Sử dụng {@code field-name} làm aggregated search field.
 *
 * @see PreventSearchQueryBuilder
 * @see PreventSearchResultDTO
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

  private final ElasticsearchOperations elasticsearchOperations;

  /**
   * Tra cứu thông tin ngăn chặn với phân trang.
   *
   * <p>Query được xây dựng động dựa trên type và keyword. Kết quả được map sang DTO để tách biệt
   * document layer và response layer.
   *
   * <p><strong>Ví dụ API:</strong>
   *
   * <pre>{@code GET /api/search/prevent?type=and&keyword=nguyen&page=0&size=20}</pre>
   *
   * @param type loại query ("and" hoặc "or")
   * @param keyword từ khóa tìm kiếm
   * @param page số trang (0-based)
   * @param size kích thước trang (1-100)
   * @return kết quả tra cứu có phân trang dạng {@link PageResponse}
   */
  @Override
  public SearchPreventResponse search(
      String type, String keyword, int page, int size) {

    log.info("Tra cứu Prevent - type: {}, keyword: {}, page: {}, size: {}", type, keyword, page, size);

    // 1. Parse query type
    QueryType queryType = QueryType.fromValue(type);
    boolean hasKeyword = StringUtils.hasText(keyword);

    // 2. Build query using PreventSearchQueryBuilder
    PreventSearchQueryBuilder queryBuilder = new PreventSearchQueryBuilder();

    // Wildcard detection: nếu keyword chứa * thì dùng wildcard
    if (hasKeyword && keyword.contains("*")) {
      queryBuilder.withWildcard(keyword);
    } else {
      queryBuilder.withKeyword(queryType, keyword);
    }

    // 3. Pagination: sort by score nếu có keyword, ngược lại sort by ID
    queryBuilder.withPagination(page, size, hasKeyword);

    // 4. Execute search
    SearchHits<PreventES> searchHits =
        elasticsearchOperations.search(queryBuilder.build(), PreventES.class);

    // 5. Map entity -> DTO
    List<PreventSearchResultDTO> results =
        searchHits.getSearchHits().stream()
            .map(this::toSearchResultDTO)
            .collect(Collectors.toList());

    long totalElements = searchHits.getTotalHits();
    int pageSize = normalizePageSize(size);

    log.info(
        "Tra cứu hoàn tất - total: {}, returned: {}",
        totalElements,
        results.size());

    int totalPages = (int) Math.ceil((double) totalElements / pageSize);
    int currentPage = page + 1;

    return SearchPreventResponse.builder()
        .data(results)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .currentPage(currentPage)
        .pageSize(pageSize)
        .hasNext(currentPage < totalPages)
        .hasPrevious(currentPage > 1)
        .build();
  }

  /**
   * Map {@link PreventES} entity sang {@link PreventSearchResultDTO}.
   *
   * @param hit SearchHit chứa document
   * @return DTO đã map
   */
  private PreventSearchResultDTO toSearchResultDTO(SearchHit<PreventES> hit) {
    PreventES doc = hit.getContent();
    return PreventSearchResultDTO.builder()
        .id(doc.getId())
        .dataPreventES(doc.getDataPreventES())
        .personalsES(doc.getPersonalsES())
        .organizationsES(doc.getOrganizationsES())
        .build();
  }

  /**
   * Normalize kích thước trang, đảm bảo nằm trong giới hạn cho phép.
   *
   * @param size kích thước trang yêu cầu
   * @return kích thước trang đã normalize
   */
  private int normalizePageSize(int size) {
    if (size <= 0) {
      return ConfigConstant.DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, ConfigConstant.MAX_PAGE_SIZE);
  }
}
