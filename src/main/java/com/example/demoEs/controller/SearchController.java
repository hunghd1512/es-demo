package com.example.demoEs.controller;

import com.example.demoEs.es.document.PreventES;
import com.example.demoEs.es.dto.SearchSuggestionDTO;
import com.example.demoEs.service.SearchService;
import com.example.demoEs.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;
    private final SuggestionService suggestionService;

    @GetMapping("/prevent")
    public ResponseEntity<List<PreventES>> searchPrevent(
            @RequestParam(required = false, defaultValue = "and") String type,
            @RequestParam(required = false) String keyword) {
        List<PreventES> results = searchService.search(type, keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<SearchSuggestionDTO>> getSuggestions(
            @RequestParam String indexName,
            @RequestParam String fieldName,
            @RequestParam String prefix,
            @RequestParam(required = false, defaultValue = "10") int size) {
        List<SearchSuggestionDTO> suggestions = suggestionService.getSuggestions(indexName, fieldName, prefix, size);
        return ResponseEntity.ok(suggestions);
    }
}