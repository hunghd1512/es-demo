package com.example.demoEs.service;

import com.example.demoEs.es.dto.SearchSuggestionDTO;
import java.util.List;

public interface SuggestionService {
    List<SearchSuggestionDTO> getSuggestions(String indexName, String fieldName, String prefix, int size);
}