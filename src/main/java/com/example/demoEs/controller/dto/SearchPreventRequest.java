package com.example.demoEs.controller.dto;

import com.example.demoEs.constant.QueryType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPreventRequest {

  @Builder.Default
  private QueryType type = QueryType.AND;

  @Size(max = 500, message = "Từ khóa tìm kiếm không được vượt quá 500 ký tự")
  private String keyword;

  @Min(value = 0, message = "Số trang không được nhỏ hơn 0")
  @Builder.Default
  private int page = 0;

  @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
  @Max(value = 100, message = "Kích thước trang không được vượt quá 100")
  @Builder.Default
  private int size = 20;
}
