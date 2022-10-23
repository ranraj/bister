package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.ProductAttribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductAttribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductAttributeRepository extends ReactiveCrudRepository<ProductAttribute, Long>, ProductAttributeRepositoryInternal {
    Flux<ProductAttribute> findAllBy(Pageable pageable);

    @Override
    <S extends ProductAttribute> Mono<S> save(S entity);

    @Override
    Flux<ProductAttribute> findAll();

    @Override
    Mono<ProductAttribute> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductAttributeRepositoryInternal {
    <S extends ProductAttribute> Mono<S> save(S entity);

    Flux<ProductAttribute> findAllBy(Pageable pageable);

    Flux<ProductAttribute> findAll();

    Mono<ProductAttribute> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProductAttribute> findAllBy(Pageable pageable, Criteria criteria);

}
