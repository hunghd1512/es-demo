package com.example.demoEs.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.demoEs.es.document.PreventES;
import com.example.demoEs.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<PreventES> search(String type, String keyword) {
        ArrayList<Query> mustQuery = new ArrayList<>();
        List<Query> shouldQuery = new ArrayList<>();
        List<Query> filterQuery = new ArrayList<>();
        List<Query> mustNotQuery = new ArrayList<>();

        if (type.equals("or") && keyword != null) {
            shouldQuery.add(
                    Query.of(
                            builder -> builder.wildcard(
                                    word -> word.field("field-name")
                                            .value(keyword.toLowerCase().trim())
                            )
                    )
            );
        }
        if (type.equals("and") && keyword != null) {
            mustQuery.add(
                    Query.of(
                            builder -> builder.match(
                                    word -> word.field("field-name")
                                            .query(keyword.toLowerCase().trim())
                            )
                    )
            );
        }

        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();

        Pageable pageable = PageRequest.of(0, 20)
                .withSort(Sort.by(Sort.Direction.DESC, "id"));
//        Highlight thường để ui xử lý , để Be xử lý sẽ nặng về response

        nativeQueryBuilder.withQuery(query -> query.bool(builder -> {
                            if (!mustQuery.isEmpty()) builder.must(mustQuery);
                            if (!mustNotQuery.isEmpty()) builder.mustNot(mustNotQuery);
                            if (!filterQuery.isEmpty()) builder.filter(filterQuery);
                            if (!shouldQuery.isEmpty()) builder.should(shouldQuery)
                                    .minimumShouldMatch("1");
                            return builder;
                        }
                )
        ).withPageable(pageable);

        SearchHits<PreventES> preventESSearchHit = elasticsearchOperations.search(nativeQueryBuilder.build(), PreventES.class);
        return preventESSearchHit.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
