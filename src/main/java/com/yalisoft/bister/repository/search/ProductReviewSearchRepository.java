package com.yalisoft.bister.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yalisoft.bister.domain.ProductReview;
import com.yalisoft.bister.repository.ProductReviewRepository;
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
 * Spring Data Elasticsearch repository for the {@link ProductReview} entity.
 */
public interface ProductReviewSearchRepository
    extends ReactiveElasticsearchRepository<ProductReview, Long>, ProductReviewSearchRepositoryInternal {}

interface ProductReviewSearchRepositoryInternal {
    Flux<ProductReview> search(String query, Pageable pageable);

    Flux<ProductReview> search(Query query);
}

class ProductReviewSearchRepositoryInternalImpl implements ProductReviewSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ProductReviewSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ProductReview> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<ProductReview> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, ProductReview.class).map(SearchHit::getContent);
    }
}
