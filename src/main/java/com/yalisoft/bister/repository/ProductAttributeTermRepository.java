package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductAttributeTerm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductAttributeTerm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductAttributeTermRepository
    extends ReactiveCrudRepository<ProductAttributeTerm, Long>, ProductAttributeTermRepositoryInternal {
    Flux<ProductAttributeTerm> findAllBy(Pageable pageable);

    @Override
    Mono<ProductAttributeTerm> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductAttributeTerm> findAllWithEagerRelationships();

    @Override
    Flux<ProductAttributeTerm> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_attribute_term entity WHERE entity.product_attribute_id = :id")
    Flux<ProductAttributeTerm> findByProductAttribute(Long id);

    @Query("SELECT * FROM product_attribute_term entity WHERE entity.product_attribute_id IS NULL")
    Flux<ProductAttributeTerm> findAllWhereProductAttributeIsNull();

    @Query("SELECT * FROM product_attribute_term entity WHERE entity.product_id = :id")
    Flux<ProductAttributeTerm> findByProduct(Long id);

    @Query("SELECT * FROM product_attribute_term entity WHERE entity.product_id IS NULL")
    Flux<ProductAttributeTerm> findAllWhereProductIsNull();

    @Override
    <S extends ProductAttributeTerm> Mono<S> save(S entity);

    @Override
    Flux<ProductAttributeTerm> findAll();

    @Override
    Mono<ProductAttributeTerm> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductAttributeTermRepositoryInternal {
    <S extends ProductAttributeTerm> Mono<S> save(S entity);

    Flux<ProductAttributeTerm> findAllBy(Pageable pageable);

    Flux<ProductAttributeTerm> findAll();

    Mono<ProductAttributeTerm> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductAttributeTerm> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductAttributeTerm> findOneWithEagerRelationships(Long id);

    Flux<ProductAttributeTerm> findAllWithEagerRelationships();

    Flux<ProductAttributeTerm> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
