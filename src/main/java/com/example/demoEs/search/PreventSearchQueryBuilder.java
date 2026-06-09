package com.example.demoEs.search;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.example.demoEs.constant.QueryType;
import com.example.demoEs.util.VietnameseTextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class để xây dựng Elasticsearch {@link NativeQuery} cho chức năng tra cứu Prevent.
 *
 * <p>Hỗ trợ các tính năng tìm kiếm nâng cao:
 *
 * <ul>
 *   <li>Phrase search 2-tier: accent-sensitive + accent-insensitive (hỗ trợ tiếng Việt)
 *   <li>Wildcard search cho prefix/suffix matching
 *   <li>Boolean query kết hợp (must/should/filter)
 *   <li>Dynamic pagination và sorting
 *   <li>Dynamic sort: score hoặc field
 * </ul>
 */
@Slf4j
public class PreventSearchQueryBuilder {

  // ======================== BOOLEAN CONSTANTS ========================
  private static final String MINIMUM_SHOULD_MATCH = "1";

  // ======================== FIELD NAME CONSTANTS ========================
  private static final String FIELD_SEARCH_CONTENT = "field-name";
  private static final String FIELD_ID = "id";

  // ======================== BOOST CONSTANTS ========================
  private static final float WILDCARD_BOOST = 1.5f;

  // ======================== PAGINATION CONSTANTS ========================
  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final int MAX_PAGE_SIZE = 100;
  private static final int PAGE_OFFSET = 1;

  // ======================== INSTANCE FIELDS ========================
  private final NativeQueryBuilder queryBuilder;
  private final BoolQuery.Builder boolQueryBuilder;
  private final List<Query> mustQueries = new ArrayList<>();
  private final List<Query> shouldQueries = new ArrayList<>();
  private final List<Query> filterQueries = new ArrayList<>();

  public PreventSearchQueryBuilder() {
    this.queryBuilder = new NativeQueryBuilder();
    this.boolQueryBuilder = new BoolQuery.Builder();
  }

  /**
   * Thêm search query theo type (AND/OR) và keyword.
   *
   * <p>Sử dụng 2-tier matching cho cả AND và OR:
   *
   * <ul>
   *   <li>Tier 1: Accent-sensitive match (exact match, boost cao)
   *   <li>Tier 2: Accent-insensitive match (loại bỏ dấu tiếng Việt, boost thấp hơn)
   * </ul>
   *
   * @param type loại query (AND = must, OR = should)
   * @param keyword từ khóa tìm kiếm
   * @return Builder instance
   */
  public PreventSearchQueryBuilder withKeyword(QueryType type, String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return this;
    }

    String trimmed = keyword.trim().toLowerCase();
    List<Query> tierQueries = new ArrayList<>();

    // Tier 1: Accent-sensitive (exact match)
    tierQueries.add(buildMatchQuery(FIELD_SEARCH_CONTENT, trimmed, 1.0f));

    // Tier 2: Accent-insensitive (Vietnamese accent removal)
    String normalized = VietnameseTextUtils.removeAccents(trimmed);
    if (!normalized.equals(trimmed)) {
      tierQueries.add(buildMatchQuery(FIELD_SEARCH_CONTENT, normalized, 1.0f));
    }

    if (type == QueryType.AND) {
      for (Query q : tierQueries) {
        mustQueries.add(q);
      }
    } else {
      // OR mode: wrap tier queries với minimumShouldMatch
      Query orQuery = BoolQuery.of(b -> b.should(tierQueries).minimumShouldMatch(MINIMUM_SHOULD_MATCH))
          ._toQuery();
      shouldQueries.add(orQuery);
    }

    return this;
  }

  /**
   * Thêm wildcard search query.
   *
   * @param keyword pattern wildcard (ví dụ: "nguyen*", "*trung*")
   * @return Builder instance
   */
  public PreventSearchQueryBuilder withWildcard(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return this;
    }

    String trimmed = keyword.trim().toLowerCase();

    shouldQueries.add(
        WildcardQuery.of(w -> w
            .field(FIELD_SEARCH_CONTENT)
            .value(trimmed)
            .caseInsensitive(true)
            .boost(WILDCARD_BOOST))._toQuery());

    return this;
  }

  /**
   * Thêm pagination và sorting.
   *
   * @param page số trang (1-based)
   * @param size kích thước trang
   * @param sortByScore nếu true, sort theo relevance score; ngược lại theo ModifiedAt
   * @return Builder instance
   */
  public PreventSearchQueryBuilder withPagination(int page, int size, boolean sortByScore) {
    int pageNumber = (page > 0) ? page - PAGE_OFFSET : 0;
    int pageSize = (size > 0) ? size : DEFAULT_PAGE_SIZE;
    pageSize = Math.min(pageSize, MAX_PAGE_SIZE);

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    queryBuilder.withPageable(pageable);

    if (sortByScore) {
      queryBuilder.withSort(s -> s.score(sc -> sc.order(SortOrder.Desc)));
    }
    queryBuilder.withSort(s -> s.field(f -> f.field(FIELD_ID).order(SortOrder.Desc)));

    return this;
  }

  /**
   * Build và trả về {@link NativeQuery}.
   *
   * @return NativeQuery đã được build
   */
  public NativeQuery build() {
    if (!mustQueries.isEmpty()) {
      boolQueryBuilder.must(mustQueries);
    }
    if (!shouldQueries.isEmpty()) {
      boolQueryBuilder.should(shouldQueries);
      boolQueryBuilder.minimumShouldMatch(MINIMUM_SHOULD_MATCH);
    }
    if (!filterQueries.isEmpty()) {
      boolQueryBuilder.filter(filterQueries);
    }

    BoolQuery boolQuery = boolQueryBuilder.build();
    queryBuilder.withQuery(boolQuery._toQuery());

    log.debug("Built Elasticsearch query: {}", boolQuery);
    return queryBuilder.build();
  }

  /**
   * Build Match query đơn giản.
   *
   * @param field tên field
   * @param query giá trị query
   * @param boost boost factor
   * @return Query
   */
  private Query buildMatchQuery(String field, String query, float boost) {
    return MatchQuery.of(m -> m.field(field).query(query).boost(boost))._toQuery();
  }
}
