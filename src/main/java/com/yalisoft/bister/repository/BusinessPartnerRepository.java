package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.BusinessPartner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BusinessPartner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessPartnerRepository extends ReactiveCrudRepository<BusinessPartner, Long>, BusinessPartnerRepositoryInternal {
    Flux<BusinessPartner> findAllBy(Pageable pageable);

    @Override
    <S extends BusinessPartner> Mono<S> save(S entity);

    @Override
    Flux<BusinessPartner> findAll();

    @Override
    Mono<BusinessPartner> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BusinessPartnerRepositoryInternal {
    <S extends BusinessPartner> Mono<S> save(S entity);

    Flux<BusinessPartner> findAllBy(Pageable pageable);

    Flux<BusinessPartner> findAll();

    Mono<BusinessPartner> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BusinessPartner> findAllBy(Pageable pageable, Criteria criteria);

}
