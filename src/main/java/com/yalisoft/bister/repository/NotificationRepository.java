package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long>, NotificationRepositoryInternal {
    Flux<Notification> findAllBy(Pageable pageable);

    @Override
    Mono<Notification> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Notification> findAllWithEagerRelationships();

    @Override
    Flux<Notification> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM notification entity WHERE entity.agent_id = :id")
    Flux<Notification> findByAgent(Long id);

    @Query("SELECT * FROM notification entity WHERE entity.agent_id IS NULL")
    Flux<Notification> findAllWhereAgentIsNull();

    @Query("SELECT * FROM notification entity WHERE entity.user_id = :id")
    Flux<Notification> findByUser(Long id);

    @Query("SELECT * FROM notification entity WHERE entity.user_id IS NULL")
    Flux<Notification> findAllWhereUserIsNull();

    @Override
    <S extends Notification> Mono<S> save(S entity);

    @Override
    Flux<Notification> findAll();

    @Override
    Mono<Notification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NotificationRepositoryInternal {
    <S extends Notification> Mono<S> save(S entity);

    Flux<Notification> findAllBy(Pageable pageable);

    Flux<Notification> findAll();

    Mono<Notification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notification> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Notification> findOneWithEagerRelationships(Long id);

    Flux<Notification> findAllWithEagerRelationships();

    Flux<Notification> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
