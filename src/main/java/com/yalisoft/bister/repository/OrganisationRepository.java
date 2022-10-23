package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Organisation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Organisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganisationRepository extends ReactiveCrudRepository<Organisation, Long>, OrganisationRepositoryInternal {
    Flux<Organisation> findAllBy(Pageable pageable);

    @Override
    Mono<Organisation> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Organisation> findAllWithEagerRelationships();

    @Override
    Flux<Organisation> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM organisation entity WHERE entity.address_id = :id")
    Flux<Organisation> findByAddress(Long id);

    @Query("SELECT * FROM organisation entity WHERE entity.address_id IS NULL")
    Flux<Organisation> findAllWhereAddressIsNull();

    @Query("SELECT * FROM organisation entity WHERE entity.business_partner_id = :id")
    Flux<Organisation> findByBusinessPartner(Long id);

    @Query("SELECT * FROM organisation entity WHERE entity.business_partner_id IS NULL")
    Flux<Organisation> findAllWhereBusinessPartnerIsNull();

    @Override
    <S extends Organisation> Mono<S> save(S entity);

    @Override
    Flux<Organisation> findAll();

    @Override
    Mono<Organisation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrganisationRepositoryInternal {
    <S extends Organisation> Mono<S> save(S entity);

    Flux<Organisation> findAllBy(Pageable pageable);

    Flux<Organisation> findAll();

    Mono<Organisation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Organisation> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Organisation> findOneWithEagerRelationships(Long id);

    Flux<Organisation> findAllWithEagerRelationships();

    Flux<Organisation> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
