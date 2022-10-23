package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long>, TransactionRepositoryInternal {
    Flux<Transaction> findAllBy(Pageable pageable);

    @Query("SELECT * FROM transaction entity WHERE entity.purchase_order_id = :id")
    Flux<Transaction> findByPurchaseOrder(Long id);

    @Query("SELECT * FROM transaction entity WHERE entity.purchase_order_id IS NULL")
    Flux<Transaction> findAllWherePurchaseOrderIsNull();

    @Override
    <S extends Transaction> Mono<S> save(S entity);

    @Override
    Flux<Transaction> findAll();

    @Override
    Mono<Transaction> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TransactionRepositoryInternal {
    <S extends Transaction> Mono<S> save(S entity);

    Flux<Transaction> findAllBy(Pageable pageable);

    Flux<Transaction> findAll();

    Mono<Transaction> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Transaction> findAllBy(Pageable pageable, Criteria criteria);

}
