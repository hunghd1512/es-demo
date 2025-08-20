package com.example.demoEs.es.repository.es;

import com.example.demoEs.es.document.OrganizationDataEntryES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrganizationDataEntryESRepository extends ElasticsearchRepository<OrganizationDataEntryES, Long> {
}
