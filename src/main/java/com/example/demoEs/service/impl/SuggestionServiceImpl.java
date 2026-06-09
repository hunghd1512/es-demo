package com.example.demoEs.service.impl;

import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.example.demoEs.es.dto.SearchSuggestionDTO;
import com.example.demoEs.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation xử lý autocomplete suggestions từ Elasticsearch.
 *
 * <p>Sử dụng Elasticsearch Completion Suggester để cung cấp gợi ý nhanh khi người dùng nhập liệu.
 *
 * @see SuggestionService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionServiceImpl implements SuggestionService {

  private final ElasticsearchOperations elasticsearchOperations;

  @Override
  public List<SearchSuggestionDTO> getSuggestions(
      String indexName, String fieldName, String prefix, int size) {
    try {
      String suggesterName = fieldName + "_suggest";

      Suggester suggester =
          Suggester.of(
              s ->
                  s.suggesters(
                      suggesterName,
                      fs ->
                          fs
                              .prefix(prefix)
                              .completion(
                                  c ->
                                      c.field(fieldName + ".suggest")
                                          .size(size)
                                          .skipDuplicates(true))));

      NativeQuery query = NativeQuery.builder().withSuggester(suggester).build();

      @SuppressWarnings("rawtypes")
      SearchHits<Map> searchHits = elasticsearchOperations.search(query, Map.class);

      List<String> suggestionTexts = new ArrayList<>();

      for (SearchHit<Map> hit : searchHits.getSearchHits()) {
        Map<String, Object> source = hit.getContent();
        if (source != null && source.containsKey(fieldName + "_suggest")) {
          Object rawSuggest = source.get(fieldName + "_suggest");
          if (rawSuggest instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> suggestList = (List<Map<String, Object>>) rawSuggest;
            for (Map<String, Object> suggest : suggestList) {
              if (suggest.containsKey("options")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> options = (List<Map<String, Object>>) suggest.get("options");
                for (Map<String, Object> option : options) {
                  if (option.containsKey("text")) {
                    suggestionTexts.add(option.get("text").toString());
                  }
                }
              }
            }
          }
        }
      }

      return List.of(
          SearchSuggestionDTO.builder()
              .field(fieldName)
              .suggestions(
                  suggestionTexts.stream().distinct().collect(Collectors.toList()))
              .build());
    } catch (Exception e) {
      log.error(
          "Lỗi khi lấy suggestions từ index '{}', field '{}': {}",
          indexName,
          fieldName,
          e.getMessage());
      return new ArrayList<>();
    }
  }
}
