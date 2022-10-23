package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Refund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Refund entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RefundRepository extends ReactiveCrudRepository<Refund, Long>, RefundRepositoryInternal {
    Flux<Refund> findAllBy(Pageable pageable);

    @Override
    Mono<Refund> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Refund> findAllWithEagerRelationships();

    @Override
    Flux<Refund> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM refund entity WHERE entity.transaction_id = :id")
    Flux<Refund> findByTransaction(Long id);

    @Query("SELECT * FROM refund entity WHERE entity.transaction_id IS NULL")
    Flux<Refund> findAllWhereTransactionIsNull();

    @Query("SELECT * FROM refund entity WHERE entity.user_id = :id")
    Flux<Refund> findByUser(Long id);

    @Query("SELECT * FROM refund entity WHERE entity.user_id IS NULL")
    Flux<Refund> findAllWhereUserIsNull();

    @Override
    <S extends Refund> Mono<S> save(S entity);

    @Override
    Flux<Refund> findAll();

    @Override
    Mono<Refund> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RefundRepositoryInternal {
    <S extends Refund> Mono<S> save(S entity);

    Flux<Refund> findAllBy(Pageable pageable);

    Flux<Refund> findAll();

    Mono<Refund> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Refund> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Refund> findOneWithEagerRelationships(Long id);

    Flux<Refund> findAllWithEagerRelationships();

    Flux<Refund> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
