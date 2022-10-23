package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.EnquiryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EnquiryResponse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnquiryResponseRepository extends ReactiveCrudRepository<EnquiryResponse, Long>, EnquiryResponseRepositoryInternal {
    Flux<EnquiryResponse> findAllBy(Pageable pageable);

    @Override
    Mono<EnquiryResponse> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EnquiryResponse> findAllWithEagerRelationships();

    @Override
    Flux<EnquiryResponse> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM enquiry_response entity WHERE entity.agent_id = :id")
    Flux<EnquiryResponse> findByAgent(Long id);

    @Query("SELECT * FROM enquiry_response entity WHERE entity.agent_id IS NULL")
    Flux<EnquiryResponse> findAllWhereAgentIsNull();

    @Query("SELECT * FROM enquiry_response entity WHERE entity.enquiry_id = :id")
    Flux<EnquiryResponse> findByEnquiry(Long id);

    @Query("SELECT * FROM enquiry_response entity WHERE entity.enquiry_id IS NULL")
    Flux<EnquiryResponse> findAllWhereEnquiryIsNull();

    @Override
    <S extends EnquiryResponse> Mono<S> save(S entity);

    @Override
    Flux<EnquiryResponse> findAll();

    @Override
    Mono<EnquiryResponse> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnquiryResponseRepositoryInternal {
    <S extends EnquiryResponse> Mono<S> save(S entity);

    Flux<EnquiryResponse> findAllBy(Pageable pageable);

    Flux<EnquiryResponse> findAll();

    Mono<EnquiryResponse> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EnquiryResponse> findAllBy(Pageable pageable, Criteria criteria);

    Mono<EnquiryResponse> findOneWithEagerRelationships(Long id);

    Flux<EnquiryResponse> findAllWithEagerRelationships();

    Flux<EnquiryResponse> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
