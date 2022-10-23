package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long>, InvoiceRepositoryInternal {
    Flux<Invoice> findAllBy(Pageable pageable);

    @Query("SELECT * FROM invoice entity WHERE entity.purchase_order_id = :id")
    Flux<Invoice> findByPurchaseOrder(Long id);

    @Query("SELECT * FROM invoice entity WHERE entity.purchase_order_id IS NULL")
    Flux<Invoice> findAllWherePurchaseOrderIsNull();

    @Query("SELECT * FROM invoice entity WHERE entity.id not in (select payment_schedule_id from payment_schedule)")
    Flux<Invoice> findAllWherePaymentScheduleIsNull();

    @Override
    <S extends Invoice> Mono<S> save(S entity);

    @Override
    Flux<Invoice> findAll();

    @Override
    Mono<Invoice> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InvoiceRepositoryInternal {
    <S extends Invoice> Mono<S> save(S entity);

    Flux<Invoice> findAllBy(Pageable pageable);

    Flux<Invoice> findAll();

    Mono<Invoice> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Invoice> findAllBy(Pageable pageable, Criteria criteria);

}
