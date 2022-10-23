package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductSpecification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductSpecificationRepository
    extends ReactiveCrudRepository<ProductSpecification, Long>, ProductSpecificationRepositoryInternal {
    Flux<ProductSpecification> findAllBy(Pageable pageable);

    @Override
    Mono<ProductSpecification> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductSpecification> findAllWithEagerRelationships();

    @Override
    Flux<ProductSpecification> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_specification entity WHERE entity.product_specification_group_id = :id")
    Flux<ProductSpecification> findByProductSpecificationGroup(Long id);

    @Query("SELECT * FROM product_specification entity WHERE entity.product_specification_group_id IS NULL")
    Flux<ProductSpecification> findAllWhereProductSpecificationGroupIsNull();

    @Query("SELECT * FROM product_specification entity WHERE entity.product_id = :id")
    Flux<ProductSpecification> findByProduct(Long id);

    @Query("SELECT * FROM product_specification entity WHERE entity.product_id IS NULL")
    Flux<ProductSpecification> findAllWhereProductIsNull();

    @Override
    <S extends ProductSpecification> Mono<S> save(S entity);

    @Override
    Flux<ProductSpecification> findAll();

    @Override
    Mono<ProductSpecification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductSpecificationRepositoryInternal {
    <S extends ProductSpecification> Mono<S> save(S entity);

    Flux<ProductSpecification> findAllBy(Pageable pageable);

    Flux<ProductSpecification> findAll();

    Mono<ProductSpecification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductSpecification> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductSpecification> findOneWithEagerRelationships(Long id);

    Flux<ProductSpecification> findAllWithEagerRelationships();

    Flux<ProductSpecification> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
