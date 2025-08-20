package com.example.demoEs.es.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestionDTO {
    private String field;
    private List<String> suggestions;
}