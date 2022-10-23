package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectSpecificationGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectSpecificationGroupRepository
    extends ReactiveCrudRepository<ProjectSpecificationGroup, Long>, ProjectSpecificationGroupRepositoryInternal {
    Flux<ProjectSpecificationGroup> findAllBy(Pageable pageable);

    @Override
    Mono<ProjectSpecificationGroup> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProjectSpecificationGroup> findAllWithEagerRelationships();

    @Override
    Flux<ProjectSpecificationGroup> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM project_specification_group entity WHERE entity.project_id = :id")
    Flux<ProjectSpecificationGroup> findByProject(Long id);

    @Query("SELECT * FROM project_specification_group entity WHERE entity.project_id IS NULL")
    Flux<ProjectSpecificationGroup> findAllWhereProjectIsNull();

    @Override
    <S extends ProjectSpecificationGroup> Mono<S> save(S entity);

    @Override
    Flux<ProjectSpecificationGroup> findAll();

    @Override
    Mono<ProjectSpecificationGroup> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectSpecificationGroupRepositoryInternal {
    <S extends ProjectSpecificationGroup> Mono<S> save(S entity);

    Flux<ProjectSpecificationGroup> findAllBy(Pageable pageable);

    Flux<ProjectSpecificationGroup> findAll();

    Mono<ProjectSpecificationGroup> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectSpecificationGroup> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProjectSpecificationGroup> findOneWithEagerRelationships(Long id);

    Flux<ProjectSpecificationGroup> findAllWithEagerRelationships();

    Flux<ProjectSpecificationGroup> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
