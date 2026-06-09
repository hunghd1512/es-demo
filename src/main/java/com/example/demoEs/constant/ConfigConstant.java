package com.example.demoEs.constant;

/**
 * Constants cấu hình ứng dụng.
 */
public final class ConfigConstant {

  private ConfigConstant() {
    throw new UnsupportedOperationException("Constants class cannot be instantiated");
  }

  // ======================== Elasticsearch ========================
  /** Tên analyzer sử dụng trong Elasticsearch mapping. */
  public static final String ANALYZE_ES = "analyzer";

  /** Tên field search content trong PreventES document. */
  public static final String ES_FIELD_SEARCH_CONTENT = "field-name";

  /** Tên field ID trong Elasticsearch documents. */
  public static final String ES_FIELD_ID = "id";

  /** Tên field ModifiedAt để sort. */
  public static final String ES_FIELD_MODIFIED_AT = "modifiedAt";

  // ======================== Pagination ========================
  /** Kích thước trang mặc định. */
  public static final int DEFAULT_PAGE_SIZE = 20;

  /** Kích thước trang tối đa. */
  public static final int MAX_PAGE_SIZE = 100;

  /** Offset để chuyển từ 1-based (API) sang 0-based (Spring Data). */
  public static final int PAGE_OFFSET = 1;
}
