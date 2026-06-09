package com.example.demoEs.controller;

import com.example.demoEs.controller.dto.SearchPreventRequest;
import com.example.demoEs.controller.dto.SearchPreventResponse;
import com.example.demoEs.es.dto.SearchSuggestionDTO;
import com.example.demoEs.service.SearchService;
import com.example.demoEs.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các endpoint tra cứu thông tin ngăn chặn.
 *
 * <p>Base path: {@code /api/search}
 *
 * @see SearchService
 * @see SuggestionService
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {

  private final SearchService searchService;
  private final SuggestionService suggestionService;

  /**
   * Tra cứu thông tin ngăn chặn với phân trang.
   *
   * <p>Hỗ trợ:
   *
   * <ul>
   *   <li>AND mode: Tất cả điều kiện phải match
   *   <li>OR mode: Bất kỳ điều kiện nào match
   *   <li>Tìm kiếm không dấu tiếng Việt
   *   <li>Wildcard search (prefix/suffix với dấu *)
   * </ul>
   *
   * <p><strong>Ví dụ:</strong>
   *
   * <pre>{@code GET /api/search/prevent?type=and&keyword=nguyen&page=0&size=20}</pre>
   *
   * @param request thông số tìm kiếm (type, keyword, page, size)
   * @return kết quả tra cứu có phân trang
   */
  @GetMapping("/prevent")
  public ResponseEntity<SearchPreventResponse> searchPrevent(
      @Valid @ModelAttribute SearchPreventRequest request) {
    SearchPreventResponse response =
        searchService.search(
            request.getType().getValue(),
            request.getKeyword(),
            request.getPage(),
            request.getSize());
    return ResponseEntity.ok(response);
  }

  /**
   * Lấy danh sách gợi ý autocomplete.
   *
   * @param indexName tên Elasticsearch index
   * @param fieldName tên field chứa completion suggester
   * @param prefix prefix nhập vào để gợi ý
   * @param size số lượng gợi ý tối đa
   * @return danh sách gợi ý
   */
  @GetMapping("/suggestions")
  public ResponseEntity<List<SearchSuggestionDTO>> getSuggestions(
      @RequestParam String indexName,
      @RequestParam String fieldName,
      @RequestParam String prefix,
      @RequestParam(required = false, defaultValue = "10") int size) {
    List<SearchSuggestionDTO> suggestions =
        suggestionService.getSuggestions(indexName, fieldName, prefix, size);
    return ResponseEntity.ok(suggestions);
  }
}
