package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.PurchaseOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PurchaseOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseOrderRepository extends ReactiveCrudRepository<PurchaseOrder, Long>, PurchaseOrderRepositoryInternal {
    Flux<PurchaseOrder> findAllBy(Pageable pageable);

    @Override
    Mono<PurchaseOrder> findOneWithEagerRelationships(Long id);

    @Override
    Flux<PurchaseOrder> findAllWithEagerRelationships();

    @Override
    Flux<PurchaseOrder> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM purchase_order entity WHERE entity.user_id = :id")
    Flux<PurchaseOrder> findByUser(Long id);

    @Query("SELECT * FROM purchase_order entity WHERE entity.user_id IS NULL")
    Flux<PurchaseOrder> findAllWhereUserIsNull();

    @Query("SELECT * FROM purchase_order entity WHERE entity.product_variation_id = :id")
    Flux<PurchaseOrder> findByProductVariation(Long id);

    @Query("SELECT * FROM purchase_order entity WHERE entity.product_variation_id IS NULL")
    Flux<PurchaseOrder> findAllWhereProductVariationIsNull();

    @Query("SELECT * FROM purchase_order entity WHERE entity.id not in (select invoice_id from invoice)")
    Flux<PurchaseOrder> findAllWhereInvoiceIsNull();

    @Override
    <S extends PurchaseOrder> Mono<S> save(S entity);

    @Override
    Flux<PurchaseOrder> findAll();

    @Override
    Mono<PurchaseOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PurchaseOrderRepositoryInternal {
    <S extends PurchaseOrder> Mono<S> save(S entity);

    Flux<PurchaseOrder> findAllBy(Pageable pageable);

    Flux<PurchaseOrder> findAll();

    Mono<PurchaseOrder> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PurchaseOrder> findAllBy(Pageable pageable, Criteria criteria);

    Mono<PurchaseOrder> findOneWithEagerRelationships(Long id);

    Flux<PurchaseOrder> findAllWithEagerRelationships();

    Flux<PurchaseOrder> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
