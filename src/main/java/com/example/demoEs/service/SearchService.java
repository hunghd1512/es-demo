package com.example.demoEs.service;

import com.example.demoEs.es.document.PreventES;

import java.util.List;

public interface SearchService {
    List<PreventES> search(String type, String keyword);
}
