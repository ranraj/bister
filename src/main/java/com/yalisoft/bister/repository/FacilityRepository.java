package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Facility;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Facility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityRepository extends ReactiveCrudRepository<Facility, Long>, FacilityRepositoryInternal {
    Flux<Facility> findAllBy(Pageable pageable);

    @Override
    Mono<Facility> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Facility> findAllWithEagerRelationships();

    @Override
    Flux<Facility> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM facility entity WHERE entity.address_id = :id")
    Flux<Facility> findByAddress(Long id);

    @Query("SELECT * FROM facility entity WHERE entity.address_id IS NULL")
    Flux<Facility> findAllWhereAddressIsNull();

    @Query("SELECT * FROM facility entity WHERE entity.user_id = :id")
    Flux<Facility> findByUser(Long id);

    @Query("SELECT * FROM facility entity WHERE entity.user_id IS NULL")
    Flux<Facility> findAllWhereUserIsNull();

    @Query("SELECT * FROM facility entity WHERE entity.organisation_id = :id")
    Flux<Facility> findByOrganisation(Long id);

    @Query("SELECT * FROM facility entity WHERE entity.organisation_id IS NULL")
    Flux<Facility> findAllWhereOrganisationIsNull();

    @Override
    <S extends Facility> Mono<S> save(S entity);

    @Override
    Flux<Facility> findAll();

    @Override
    Mono<Facility> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FacilityRepositoryInternal {
    <S extends Facility> Mono<S> save(S entity);

    Flux<Facility> findAllBy(Pageable pageable);

    Flux<Facility> findAll();

    Mono<Facility> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Facility> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Facility> findOneWithEagerRelationships(Long id);

    Flux<Facility> findAllWithEagerRelationships();

    Flux<Facility> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
