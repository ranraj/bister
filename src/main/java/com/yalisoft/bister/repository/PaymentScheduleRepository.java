package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.PaymentSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PaymentSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentScheduleRepository extends ReactiveCrudRepository<PaymentSchedule, Long>, PaymentScheduleRepositoryInternal {
    Flux<PaymentSchedule> findAllBy(Pageable pageable);

    @Query("SELECT * FROM payment_schedule entity WHERE entity.invoice_id = :id")
    Flux<PaymentSchedule> findByInvoice(Long id);

    @Query("SELECT * FROM payment_schedule entity WHERE entity.invoice_id IS NULL")
    Flux<PaymentSchedule> findAllWhereInvoiceIsNull();

    @Query("SELECT * FROM payment_schedule entity WHERE entity.purchase_ordep_id = :id")
    Flux<PaymentSchedule> findByPurchaseOrdep(Long id);

    @Query("SELECT * FROM payment_schedule entity WHERE entity.purchase_ordep_id IS NULL")
    Flux<PaymentSchedule> findAllWherePurchaseOrdepIsNull();

    @Override
    <S extends PaymentSchedule> Mono<S> save(S entity);

    @Override
    Flux<PaymentSchedule> findAll();

    @Override
    Mono<PaymentSchedule> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PaymentScheduleRepositoryInternal {
    <S extends PaymentSchedule> Mono<S> save(S entity);

    Flux<PaymentSchedule> findAllBy(Pageable pageable);

    Flux<PaymentSchedule> findAll();

    Mono<PaymentSchedule> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PaymentSchedule> findAllBy(Pageable pageable, Criteria criteria);

}
