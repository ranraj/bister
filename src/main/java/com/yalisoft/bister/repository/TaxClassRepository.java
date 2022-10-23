package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.TaxClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TaxClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaxClassRepository extends ReactiveCrudRepository<TaxClass, Long>, TaxClassRepositoryInternal {
    Flux<TaxClass> findAllBy(Pageable pageable);

    @Override
    <S extends TaxClass> Mono<S> save(S entity);

    @Override
    Flux<TaxClass> findAll();

    @Override
    Mono<TaxClass> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TaxClassRepositoryInternal {
    <S extends TaxClass> Mono<S> save(S entity);

    Flux<TaxClass> findAllBy(Pageable pageable);

    Flux<TaxClass> findAll();

    Mono<TaxClass> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TaxClass> findAllBy(Pageable pageable, Criteria criteria);

}
