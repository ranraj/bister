package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Certification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Certification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CertificationRepository extends ReactiveCrudRepository<Certification, Long>, CertificationRepositoryInternal {
    Flux<Certification> findAllBy(Pageable pageable);

    @Override
    <S extends Certification> Mono<S> save(S entity);

    @Override
    Flux<Certification> findAll();

    @Override
    Mono<Certification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CertificationRepositoryInternal {
    <S extends Certification> Mono<S> save(S entity);

    Flux<Certification> findAllBy(Pageable pageable);

    Flux<Certification> findAll();

    Mono<Certification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Certification> findAllBy(Pageable pageable, Criteria criteria);

}
