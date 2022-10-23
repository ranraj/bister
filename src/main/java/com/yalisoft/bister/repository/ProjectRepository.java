package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long>, ProjectRepositoryInternal {
    Flux<Project> findAllBy(Pageable pageable);

    @Override
    Mono<Project> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Project> findAllWithEagerRelationships();

    @Override
    Flux<Project> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM project entity WHERE entity.address_id = :id")
    Flux<Project> findByAddress(Long id);

    @Query("SELECT * FROM project entity WHERE entity.address_id IS NULL")
    Flux<Project> findAllWhereAddressIsNull();

    @Query("SELECT * FROM project entity WHERE entity.project_type_id = :id")
    Flux<Project> findByProjectType(Long id);

    @Query("SELECT * FROM project entity WHERE entity.project_type_id IS NULL")
    Flux<Project> findAllWhereProjectTypeIsNull();

    @Query(
        "SELECT entity.* FROM project entity JOIN rel_project__category joinTable ON entity.id = joinTable.category_id WHERE joinTable.category_id = :id"
    )
    Flux<Project> findByCategory(Long id);

    @Override
    <S extends Project> Mono<S> save(S entity);

    @Override
    Flux<Project> findAll();

    @Override
    Mono<Project> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectRepositoryInternal {
    <S extends Project> Mono<S> save(S entity);

    Flux<Project> findAllBy(Pageable pageable);

    Flux<Project> findAll();

    Mono<Project> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Project> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Project> findOneWithEagerRelationships(Long id);

    Flux<Project> findAllWithEagerRelationships();

    Flux<Project> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
