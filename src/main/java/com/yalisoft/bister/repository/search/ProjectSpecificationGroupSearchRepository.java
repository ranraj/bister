package com.yalisoft.bister.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.repository.ProjectSpecificationGroupRepository;
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
 * Spring Data Elasticsearch repository for the {@link ProjectSpecificationGroup} entity.
 */
public interface ProjectSpecificationGroupSearchRepository
    extends ReactiveElasticsearchRepository<ProjectSpecificationGroup, Long>, ProjectSpecificationGroupSearchRepositoryInternal {}

interface ProjectSpecificationGroupSearchRepositoryInternal {
    Flux<ProjectSpecificationGroup> search(String query, Pageable pageable);

    Flux<ProjectSpecificationGroup> search(Query query);
}

class ProjectSpecificationGroupSearchRepositoryInternalImpl implements ProjectSpecificationGroupSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ProjectSpecificationGroupSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ProjectSpecificationGroup> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<ProjectSpecificationGroup> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, ProjectSpecificationGroup.class).map(SearchHit::getContent);
    }
}
