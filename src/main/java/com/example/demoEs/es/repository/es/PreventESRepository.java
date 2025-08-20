package com.example.demoEs.es.repository.es;

import com.example.demoEs.es.document.PreventES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PreventESRepository extends ElasticsearchRepository<PreventES, Long> {
}
