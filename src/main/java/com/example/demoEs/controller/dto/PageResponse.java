package com.example.demoEs.controller.dto;

import lombok.*;

import java.util.List;

/**
 * Response DTO cho kết quả tra cứu có phân trang.
 *
 * <p>Tuân theo Spring Data Pageable convention:
 *
 * <ul>
 *   <li>{@code page}: Số trang hiện tại (1-based, 1 = trang đầu tiên)
 *   <li>{@code size}: Số phần tử trên mỗi trang
 *   <li>{@code totalElements}: Tổng số phần tử trên tất cả các trang
 *   <li>{@code totalPages}: Tổng số trang
 *   <li>{@code hasNext}: Có trang tiếp theo không
 *   <li>{@code hasPrevious}: Có trang trước không
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

  /** Danh sách dữ liệu trang hiện tại. */
  private List<T> data;

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

  /**
   * Tạo PageResponse từ kết quả search.
   *
   * @param data danh sách dữ liệu
   * @param totalElements tổng số phần tử
   * @param page số trang hiện tại (0-based, sẽ cộng 1 để trả về)
   * @param pageSize kích thước trang
   * @return PageResponse đã build
   */
  public static <T> PageResponse<T> of(List<T> data, long totalElements, int page, int pageSize) {
    int currentPage = page + 1;
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);

    return PageResponse.<T>builder()
        .data(data)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .currentPage(currentPage)
        .pageSize(pageSize)
        .hasNext(currentPage < totalPages)
        .hasPrevious(currentPage > 1)
        .build();
  }
}
