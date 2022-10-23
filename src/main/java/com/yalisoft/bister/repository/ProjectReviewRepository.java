package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProjectReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectReviewRepository extends ReactiveCrudRepository<ProjectReview, Long>, ProjectReviewRepositoryInternal {
    Flux<ProjectReview> findAllBy(Pageable pageable);

    @Override
    Mono<ProjectReview> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProjectReview> findAllWithEagerRelationships();

    @Override
    Flux<ProjectReview> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM project_review entity WHERE entity.project_id = :id")
    Flux<ProjectReview> findByProject(Long id);

    @Query("SELECT * FROM project_review entity WHERE entity.project_id IS NULL")
    Flux<ProjectReview> findAllWhereProjectIsNull();

    @Override
    <S extends ProjectReview> Mono<S> save(S entity);

    @Override
    Flux<ProjectReview> findAll();

    @Override
    Mono<ProjectReview> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectReviewRepositoryInternal {
    <S extends ProjectReview> Mono<S> save(S entity);

    Flux<ProjectReview> findAllBy(Pageable pageable);

    Flux<ProjectReview> findAll();

    Mono<ProjectReview> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectReview> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProjectReview> findOneWithEagerRelationships(Long id);

    Flux<ProjectReview> findAllWithEagerRelationships();

    Flux<ProjectReview> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
