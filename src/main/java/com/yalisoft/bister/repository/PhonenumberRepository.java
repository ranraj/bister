package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Phonenumber;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Phonenumber entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PhonenumberRepository extends ReactiveCrudRepository<Phonenumber, Long>, PhonenumberRepositoryInternal {
    Flux<Phonenumber> findAllBy(Pageable pageable);

    @Override
    Mono<Phonenumber> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Phonenumber> findAllWithEagerRelationships();

    @Override
    Flux<Phonenumber> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM phonenumber entity WHERE entity.user_id = :id")
    Flux<Phonenumber> findByUser(Long id);

    @Query("SELECT * FROM phonenumber entity WHERE entity.user_id IS NULL")
    Flux<Phonenumber> findAllWhereUserIsNull();

    @Query("SELECT * FROM phonenumber entity WHERE entity.organisation_id = :id")
    Flux<Phonenumber> findByOrganisation(Long id);

    @Query("SELECT * FROM phonenumber entity WHERE entity.organisation_id IS NULL")
    Flux<Phonenumber> findAllWhereOrganisationIsNull();

    @Query("SELECT * FROM phonenumber entity WHERE entity.facility_id = :id")
    Flux<Phonenumber> findByFacility(Long id);

    @Query("SELECT * FROM phonenumber entity WHERE entity.facility_id IS NULL")
    Flux<Phonenumber> findAllWhereFacilityIsNull();

    @Override
    <S extends Phonenumber> Mono<S> save(S entity);

    @Override
    Flux<Phonenumber> findAll();

    @Override
    Mono<Phonenumber> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PhonenumberRepositoryInternal {
    <S extends Phonenumber> Mono<S> save(S entity);

    Flux<Phonenumber> findAllBy(Pageable pageable);

    Flux<Phonenumber> findAll();

    Mono<Phonenumber> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Phonenumber> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Phonenumber> findOneWithEagerRelationships(Long id);

    Flux<Phonenumber> findAllWithEagerRelationships();

    Flux<Phonenumber> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
