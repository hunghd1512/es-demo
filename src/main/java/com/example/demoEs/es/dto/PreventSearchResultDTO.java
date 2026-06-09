package com.example.demoEs.es.dto;

import com.example.demoEs.es.document.DataPreventES;
import com.example.demoEs.es.document.OrganizationDataEntryES;
import com.example.demoEs.es.document.PersonalDataEntryES;
import lombok.*;

import java.util.List;

/**
 * DTO chuyển đổi kết quả search từ Elasticsearch document sang response object.
 *
 * <p>Tách biệt giữa:
 *
 * <ul>
 *   <li>Document layer: Elasticsearch entities (dùng cho indexing)
 *   <li>DTO layer: Response objects (dùng cho API response)
 * </ul>
 *
 * <p>Approach này cho phép:
 *
 * <ul>
 *   <li>Kiểm soát chính xác fields trả về cho client
 *   <li>Tránh expose internal fields không cần thiết
 *   <li>Tái sử dụng mapping logic cho nhiều endpoints
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventSearchResultDTO {

  /** ID của document trong Elasticsearch. */
  private String id;

  /** Thông tin ngăn chặn chính. */
  private DataPreventES dataPreventES;

  /** Danh sách cá nhân liên quan. */
  private List<PersonalDataEntryES> personalsES;

  /** Danh sách tổ chức liên quan. */
  private List<OrganizationDataEntryES> organizationsES;
}
