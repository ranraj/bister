package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Enquiry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Enquiry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnquiryRepository extends ReactiveCrudRepository<Enquiry, Long>, EnquiryRepositoryInternal {
    Flux<Enquiry> findAllBy(Pageable pageable);

    @Override
    Mono<Enquiry> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Enquiry> findAllWithEagerRelationships();

    @Override
    Flux<Enquiry> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM enquiry entity WHERE entity.agent_id = :id")
    Flux<Enquiry> findByAgent(Long id);

    @Query("SELECT * FROM enquiry entity WHERE entity.agent_id IS NULL")
    Flux<Enquiry> findAllWhereAgentIsNull();

    @Query("SELECT * FROM enquiry entity WHERE entity.project_id = :id")
    Flux<Enquiry> findByProject(Long id);

    @Query("SELECT * FROM enquiry entity WHERE entity.project_id IS NULL")
    Flux<Enquiry> findAllWhereProjectIsNull();

    @Query("SELECT * FROM enquiry entity WHERE entity.product_id = :id")
    Flux<Enquiry> findByProduct(Long id);

    @Query("SELECT * FROM enquiry entity WHERE entity.product_id IS NULL")
    Flux<Enquiry> findAllWhereProductIsNull();

    @Query("SELECT * FROM enquiry entity WHERE entity.customer_id = :id")
    Flux<Enquiry> findByCustomer(Long id);

    @Query("SELECT * FROM enquiry entity WHERE entity.customer_id IS NULL")
    Flux<Enquiry> findAllWhereCustomerIsNull();

    @Override
    <S extends Enquiry> Mono<S> save(S entity);

    @Override
    Flux<Enquiry> findAll();

    @Override
    Mono<Enquiry> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnquiryRepositoryInternal {
    <S extends Enquiry> Mono<S> save(S entity);

    Flux<Enquiry> findAllBy(Pageable pageable);

    Flux<Enquiry> findAll();

    Mono<Enquiry> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Enquiry> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Enquiry> findOneWithEagerRelationships(Long id);

    Flux<Enquiry> findAllWithEagerRelationships();

    Flux<Enquiry> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
