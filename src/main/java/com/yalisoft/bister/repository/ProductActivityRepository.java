package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductActivity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductActivityRepository extends ReactiveCrudRepository<ProductActivity, Long>, ProductActivityRepositoryInternal {
    Flux<ProductActivity> findAllBy(Pageable pageable);

    @Override
    Mono<ProductActivity> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductActivity> findAllWithEagerRelationships();

    @Override
    Flux<ProductActivity> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_activity entity WHERE entity.product_id = :id")
    Flux<ProductActivity> findByProduct(Long id);

    @Query("SELECT * FROM product_activity entity WHERE entity.product_id IS NULL")
    Flux<ProductActivity> findAllWhereProductIsNull();

    @Override
    <S extends ProductActivity> Mono<S> save(S entity);

    @Override
    Flux<ProductActivity> findAll();

    @Override
    Mono<ProductActivity> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductActivityRepositoryInternal {
    <S extends ProductActivity> Mono<S> save(S entity);

    Flux<ProductActivity> findAllBy(Pageable pageable);

    Flux<ProductActivity> findAll();

    Mono<ProductActivity> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductActivity> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductActivity> findOneWithEagerRelationships(Long id);

    Flux<ProductActivity> findAllWithEagerRelationships();

    Flux<ProductActivity> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
