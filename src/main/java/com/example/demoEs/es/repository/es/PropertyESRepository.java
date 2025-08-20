package com.example.demoEs.es.repository.es;

import com.example.demoEs.es.document.PropertyES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface PropertyESRepository extends ElasticsearchRepository<PropertyES, Long> {
}
