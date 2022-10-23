package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductVariation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductVariationRepository extends ReactiveCrudRepository<ProductVariation, Long>, ProductVariationRepositoryInternal {
    Flux<ProductVariation> findAllBy(Pageable pageable);

    @Override
    Mono<ProductVariation> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductVariation> findAllWithEagerRelationships();

    @Override
    Flux<ProductVariation> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_variation entity WHERE entity.product_id = :id")
    Flux<ProductVariation> findByProduct(Long id);

    @Query("SELECT * FROM product_variation entity WHERE entity.product_id IS NULL")
    Flux<ProductVariation> findAllWhereProductIsNull();

    @Override
    <S extends ProductVariation> Mono<S> save(S entity);

    @Override
    Flux<ProductVariation> findAll();

    @Override
    Mono<ProductVariation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductVariationRepositoryInternal {
    <S extends ProductVariation> Mono<S> save(S entity);

    Flux<ProductVariation> findAllBy(Pageable pageable);

    Flux<ProductVariation> findAll();

    Mono<ProductVariation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductVariation> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductVariation> findOneWithEagerRelationships(Long id);

    Flux<ProductVariation> findAllWithEagerRelationships();

    Flux<ProductVariation> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
