package com.example.demoEs.es.repository.es;

import com.example.demoEs.es.document.PersonalDataEntryES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonalDataEntryESRepository extends ElasticsearchRepository<PersonalDataEntryES, Long> {
}
