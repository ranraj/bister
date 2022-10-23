package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.BookingOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BookingOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookingOrderRepository extends ReactiveCrudRepository<BookingOrder, Long>, BookingOrderRepositoryInternal {
    Flux<BookingOrder> findAllBy(Pageable pageable);

    @Override
    Mono<BookingOrder> findOneWithEagerRelationships(Long id);

    @Override
    Flux<BookingOrder> findAllWithEagerRelationships();

    @Override
    Flux<BookingOrder> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM booking_order entity WHERE entity.customer_id = :id")
    Flux<BookingOrder> findByCustomer(Long id);

    @Query("SELECT * FROM booking_order entity WHERE entity.customer_id IS NULL")
    Flux<BookingOrder> findAllWhereCustomerIsNull();

    @Query("SELECT * FROM booking_order entity WHERE entity.product_variation_id = :id")
    Flux<BookingOrder> findByProductVariation(Long id);

    @Query("SELECT * FROM booking_order entity WHERE entity.product_variation_id IS NULL")
    Flux<BookingOrder> findAllWhereProductVariationIsNull();

    @Override
    <S extends BookingOrder> Mono<S> save(S entity);

    @Override
    Flux<BookingOrder> findAll();

    @Override
    Mono<BookingOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BookingOrderRepositoryInternal {
    <S extends BookingOrder> Mono<S> save(S entity);

    Flux<BookingOrder> findAllBy(Pageable pageable);

    Flux<BookingOrder> findAll();

    Mono<BookingOrder> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BookingOrder> findAllBy(Pageable pageable, Criteria criteria);

    Mono<BookingOrder> findOneWithEagerRelationships(Long id);

    Flux<BookingOrder> findAllWithEagerRelationships();

    Flux<BookingOrder> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
