package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProjectSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectSpecification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectSpecificationRepository
    extends ReactiveCrudRepository<ProjectSpecification, Long>, ProjectSpecificationRepositoryInternal {
    Flux<ProjectSpecification> findAllBy(Pageable pageable);

    @Override
    Mono<ProjectSpecification> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProjectSpecification> findAllWithEagerRelationships();

    @Override
    Flux<ProjectSpecification> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM project_specification entity WHERE entity.project_specification_group_id = :id")
    Flux<ProjectSpecification> findByProjectSpecificationGroup(Long id);

    @Query("SELECT * FROM project_specification entity WHERE entity.project_specification_group_id IS NULL")
    Flux<ProjectSpecification> findAllWhereProjectSpecificationGroupIsNull();

    @Query("SELECT * FROM project_specification entity WHERE entity.project_id = :id")
    Flux<ProjectSpecification> findByProject(Long id);

    @Query("SELECT * FROM project_specification entity WHERE entity.project_id IS NULL")
    Flux<ProjectSpecification> findAllWhereProjectIsNull();

    @Override
    <S extends ProjectSpecification> Mono<S> save(S entity);

    @Override
    Flux<ProjectSpecification> findAll();

    @Override
    Mono<ProjectSpecification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectSpecificationRepositoryInternal {
    <S extends ProjectSpecification> Mono<S> save(S entity);

    Flux<ProjectSpecification> findAllBy(Pageable pageable);

    Flux<ProjectSpecification> findAll();

    Mono<ProjectSpecification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectSpecification> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProjectSpecification> findOneWithEagerRelationships(Long id);

    Flux<ProjectSpecification> findAllWithEagerRelationships();

    Flux<ProjectSpecification> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
