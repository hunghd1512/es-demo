package com.example.demoEs.service.impl;

import com.example.demoEs.service.ReIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.reindex.ReindexRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReIndexServiceImpl implements ReIndexService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void reIndexDataPrevent(List<String> idPrevents) {

    }
}
