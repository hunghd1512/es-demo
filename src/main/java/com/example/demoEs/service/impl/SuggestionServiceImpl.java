//package com.example.demoEs.service.impl;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
//import co.elastic.clients.elasticsearch.core.search.Suggester;
//import com.example.demoEs.es.dto.SearchSuggestionDTO;
//import com.example.demoEs.service.SuggestionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class SuggestionServiceImpl implements SuggestionService {
//
//    private final ElasticsearchClient elasticsearchClient;
//
//    @Override
//    public List<SearchSuggestionDTO> getSuggestions(String indexName, String fieldName, String prefix, int size) {
//        try {
//            // Create a suggester for the field
//            Suggester suggester = Suggester.of(s -> s
//                .suggesters(fieldName + "_suggestion", CompletionSuggester.of(cs -> cs
//                    .field(fieldName + ".suggest")
//                    .prefix(prefix)
//                    .skipDuplicates(true)
//                    .size(size)
//                ))
//            );
//
//            // Create the search request
//            SearchRequest searchRequest = SearchRequest.of(sr -> sr
//                .index(indexName)
//                .suggest(suggester)
//            );
//
//            // Execute the search
//            SearchResponse<Void> response = elasticsearchClient.search(searchRequest, Void.class);
//
//            // Process the suggestions
//            List<SearchSuggestionDTO> suggestions = new ArrayList<>();
//
//            if (response.suggest() != null) {
//                for (Map.Entry<String, List<co.elastic.clients.elasticsearch.core.search.Suggestion<Void>>> entry : response.suggest().entrySet()) {
//                    String suggestName = entry.getKey();
//                    List<co.elastic.clients.elasticsearch.core.search.Suggestion<Void>> suggestList = entry.getValue();
//
//                    List<String> suggestionTexts = suggestList.stream()
//                        .flatMap(suggestion -> suggestion.completion().options().stream())
//                        .map(option -> option.text())
//                        .distinct()
//                        .collect(Collectors.toList());
//
//                    suggestions.add(SearchSuggestionDTO.builder()
//                        .field(fieldName)
//                        .suggestions(suggestionTexts)
//                        .build());
//                }
//            }
//
//            return suggestions;
//        } catch (IOException e) {
//            throw new RuntimeException("Error getting suggestions from Elasticsearch", e);
//        }
//    }
//}