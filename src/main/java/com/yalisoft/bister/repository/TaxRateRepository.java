package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.TaxRate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TaxRate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaxRateRepository extends ReactiveCrudRepository<TaxRate, Long>, TaxRateRepositoryInternal {
    Flux<TaxRate> findAllBy(Pageable pageable);

    @Override
    Mono<TaxRate> findOneWithEagerRelationships(Long id);

    @Override
    Flux<TaxRate> findAllWithEagerRelationships();

    @Override
    Flux<TaxRate> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM tax_rate entity WHERE entity.tax_class_id = :id")
    Flux<TaxRate> findByTaxClass(Long id);

    @Query("SELECT * FROM tax_rate entity WHERE entity.tax_class_id IS NULL")
    Flux<TaxRate> findAllWhereTaxClassIsNull();

    @Override
    <S extends TaxRate> Mono<S> save(S entity);

    @Override
    Flux<TaxRate> findAll();

    @Override
    Mono<TaxRate> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TaxRateRepositoryInternal {
    <S extends TaxRate> Mono<S> save(S entity);

    Flux<TaxRate> findAllBy(Pageable pageable);

    Flux<TaxRate> findAll();

    Mono<TaxRate> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TaxRate> findAllBy(Pageable pageable, Criteria criteria);

    Mono<TaxRate> findOneWithEagerRelationships(Long id);

    Flux<TaxRate> findAllWithEagerRelationships();

    Flux<TaxRate> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
