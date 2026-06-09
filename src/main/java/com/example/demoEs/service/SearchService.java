package com.example.demoEs.service;

import com.example.demoEs.controller.dto.SearchPreventResponse;

/**
 * Service interface xử lý tra cứu thông tin ngăn chặn (Prevent) trên Elasticsearch.
 *
 * <p>Cung cấp các phương thức tra cứu với hỗ trợ:
 *
 * <ul>
 *   <li>Tra cứu AND (tất cả điều kiện phải match)
 *   <li>Tra cứu OR (bất kỳ điều kiện nào match)
 *   <li>Tra cứu wildcard (prefix/suffix matching)
 *   <li>Phân trang kết quả
 *   <li>Tìm kiếm không dấu tiếng Việt
 * </ul>
 */
public interface SearchService {

  /**
   * Tra cứu thông tin ngăn chặn.
   *
   * @param type loại query: AND (must match) hoặc OR (should match)
   * @param keyword từ khóa tìm kiếm (hỗ trợ tiếng Việt có dấu và không dấu)
   * @param page số trang (0-based)
   * @param size kích thước trang (1-100)
   * @return kết quả tra cứu có phân trang
   */
  SearchPreventResponse search(String type, String keyword, int page, int size);
}
