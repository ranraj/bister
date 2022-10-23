package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Agent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends ReactiveCrudRepository<Agent, Long>, AgentRepositoryInternal {
    Flux<Agent> findAllBy(Pageable pageable);

    @Override
    Mono<Agent> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Agent> findAllWithEagerRelationships();

    @Override
    Flux<Agent> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM agent entity WHERE entity.user_id = :id")
    Flux<Agent> findByUser(Long id);

    @Query("SELECT * FROM agent entity WHERE entity.user_id IS NULL")
    Flux<Agent> findAllWhereUserIsNull();

    @Query("SELECT * FROM agent entity WHERE entity.facility_id = :id")
    Flux<Agent> findByFacility(Long id);

    @Query("SELECT * FROM agent entity WHERE entity.facility_id IS NULL")
    Flux<Agent> findAllWhereFacilityIsNull();

    @Override
    <S extends Agent> Mono<S> save(S entity);

    @Override
    Flux<Agent> findAll();

    @Override
    Mono<Agent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AgentRepositoryInternal {
    <S extends Agent> Mono<S> save(S entity);

    Flux<Agent> findAllBy(Pageable pageable);

    Flux<Agent> findAll();

    Mono<Agent> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Agent> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Agent> findOneWithEagerRelationships(Long id);

    Flux<Agent> findAllWithEagerRelationships();

    Flux<Agent> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
