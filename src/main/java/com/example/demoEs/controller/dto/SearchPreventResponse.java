package com.example.demoEs.controller.dto;

import com.example.demoEs.es.dto.PreventSearchResultDTO;
import lombok.*;

import java.util.List;

/**
 * Response DTO cho search Prevent.
 *
 * <p>Kế thừa đầy đủ thông tin phân trang từ {@link PageResponse}. Trả về danh sách
 * {@link PreventSearchResultDTO} cùng metadata phân trang.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPreventResponse {

  /** Danh sách dữ liệu trang hiện tại. */
  private List<PreventSearchResultDTO> data;

  /** Tổng số phần tử trên tất cả các trang. */
  private long totalElements;

  /** Tổng số trang. */
  private int totalPages;

  /** Số trang hiện tại (1-based). */
  private int currentPage;

  /** Số phần tử trên trang hiện tại. */
  private int pageSize;

  /** Có trang tiếp theo không. */
  private boolean hasNext;

  /** Có trang trước không. */
  private boolean hasPrevious;
}
