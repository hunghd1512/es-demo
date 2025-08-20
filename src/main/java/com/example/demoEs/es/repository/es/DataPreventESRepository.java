package com.example.demoEs.es.repository.es;

import com.example.demoEs.es.document.DataPreventES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DataPreventESRepository extends ElasticsearchRepository<DataPreventES, Long> {
}
