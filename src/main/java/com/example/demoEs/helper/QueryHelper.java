package com.example.demoEs.helper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.example.demoEs.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueryHelper {

    private final ElasticsearchClient elasticsearchClient;

    public void updatePrevent(String indexName, String documentId, String field, String value) {
        try {
            UpdateRequest<Map<String, Object>, Map<String, Object>> request = UpdateRequest.of(u -> u
                    .index(indexName)
                    .id(documentId)
                    .doc(Map.of(field, value))
            );

            elasticsearchClient.update(request, Map.class);
            log.info("Updated document {} in index {}", documentId, indexName);
        } catch (Exception e) {
            log.error("Lỗi khi update document '{}' trong index '{}': {}", documentId, indexName, e.getMessage());
            throw new BusinessException("Có lỗi khi cập nhật dữ liệu: " + e.getMessage(), e);
        }
    }
}
