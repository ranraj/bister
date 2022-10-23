package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductVariationAttributeTerm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductVariationAttributeTermRepository
    extends ReactiveCrudRepository<ProductVariationAttributeTerm, Long>, ProductVariationAttributeTermRepositoryInternal {
    Flux<ProductVariationAttributeTerm> findAllBy(Pageable pageable);

    @Override
    Mono<ProductVariationAttributeTerm> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships();

    @Override
    Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_variation_attribute_term entity WHERE entity.product_variation_id = :id")
    Flux<ProductVariationAttributeTerm> findByProductVariation(Long id);

    @Query("SELECT * FROM product_variation_attribute_term entity WHERE entity.product_variation_id IS NULL")
    Flux<ProductVariationAttributeTerm> findAllWhereProductVariationIsNull();

    @Override
    <S extends ProductVariationAttributeTerm> Mono<S> save(S entity);

    @Override
    Flux<ProductVariationAttributeTerm> findAll();

    @Override
    Mono<ProductVariationAttributeTerm> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductVariationAttributeTermRepositoryInternal {
    <S extends ProductVariationAttributeTerm> Mono<S> save(S entity);

    Flux<ProductVariationAttributeTerm> findAllBy(Pageable pageable);

    Flux<ProductVariationAttributeTerm> findAll();

    Mono<ProductVariationAttributeTerm> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductVariationAttributeTerm> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductVariationAttributeTerm> findOneWithEagerRelationships(Long id);

    Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships();

    Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
