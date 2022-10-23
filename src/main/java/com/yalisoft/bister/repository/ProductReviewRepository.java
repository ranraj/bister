package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductReviewRepository extends ReactiveCrudRepository<ProductReview, Long>, ProductReviewRepositoryInternal {
    Flux<ProductReview> findAllBy(Pageable pageable);

    @Override
    Mono<ProductReview> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductReview> findAllWithEagerRelationships();

    @Override
    Flux<ProductReview> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_review entity WHERE entity.product_id = :id")
    Flux<ProductReview> findByProduct(Long id);

    @Query("SELECT * FROM product_review entity WHERE entity.product_id IS NULL")
    Flux<ProductReview> findAllWhereProductIsNull();

    @Override
    <S extends ProductReview> Mono<S> save(S entity);

    @Override
    Flux<ProductReview> findAll();

    @Override
    Mono<ProductReview> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductReviewRepositoryInternal {
    <S extends ProductReview> Mono<S> save(S entity);

    Flux<ProductReview> findAllBy(Pageable pageable);

    Flux<ProductReview> findAll();

    Mono<ProductReview> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductReview> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductReview> findOneWithEagerRelationships(Long id);

    Flux<ProductReview> findAllWithEagerRelationships();

    Flux<ProductReview> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
