package com.yalisoft.bister.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.repository.PaymentScheduleRepository;
import java.util.List;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PaymentSchedule} entity.
 */
public interface PaymentScheduleSearchRepository
    extends ReactiveElasticsearchRepository<PaymentSchedule, Long>, PaymentScheduleSearchRepositoryInternal {}

interface PaymentScheduleSearchRepositoryInternal {
    Flux<PaymentSchedule> search(String query, Pageable pageable);

    Flux<PaymentSchedule> search(Query query);
}

class PaymentScheduleSearchRepositoryInternalImpl implements PaymentScheduleSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PaymentScheduleSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PaymentSchedule> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<PaymentSchedule> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, PaymentSchedule.class).map(SearchHit::getContent);
    }
}
