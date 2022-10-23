package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductSpecificationGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductSpecificationGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductSpecificationGroupRepository
    extends ReactiveCrudRepository<ProductSpecificationGroup, Long>, ProductSpecificationGroupRepositoryInternal {
    Flux<ProductSpecificationGroup> findAllBy(Pageable pageable);

    @Override
    Mono<ProductSpecificationGroup> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ProductSpecificationGroup> findAllWithEagerRelationships();

    @Override
    Flux<ProductSpecificationGroup> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM product_specification_group entity WHERE entity.product_id = :id")
    Flux<ProductSpecificationGroup> findByProduct(Long id);

    @Query("SELECT * FROM product_specification_group entity WHERE entity.product_id IS NULL")
    Flux<ProductSpecificationGroup> findAllWhereProductIsNull();

    @Override
    <S extends ProductSpecificationGroup> Mono<S> save(S entity);

    @Override
    Flux<ProductSpecificationGroup> findAll();

    @Override
    Mono<ProductSpecificationGroup> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductSpecificationGroupRepositoryInternal {
    <S extends ProductSpecificationGroup> Mono<S> save(S entity);

    Flux<ProductSpecificationGroup> findAllBy(Pageable pageable);

    Flux<ProductSpecificationGroup> findAll();

    Mono<ProductSpecificationGroup> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductSpecificationGroup> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ProductSpecificationGroup> findOneWithEagerRelationships(Long id);

    Flux<ProductSpecificationGroup> findAllWithEagerRelationships();

    Flux<ProductSpecificationGroup> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
