package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProjectType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectTypeRepository extends ReactiveCrudRepository<ProjectType, Long>, ProjectTypeRepositoryInternal {
    Flux<ProjectType> findAllBy(Pageable pageable);

    @Override
    <S extends ProjectType> Mono<S> save(S entity);

    @Override
    Flux<ProjectType> findAll();

    @Override
    Mono<ProjectType> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectTypeRepositoryInternal {
    <S extends ProjectType> Mono<S> save(S entity);

    Flux<ProjectType> findAllBy(Pageable pageable);

    Flux<ProjectType> findAll();

    Mono<ProjectType> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectType> findAllBy(Pageable pageable, Criteria criteria);

}
