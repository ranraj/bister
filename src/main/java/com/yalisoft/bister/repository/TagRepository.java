package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends ReactiveCrudRepository<Tag, Long>, TagRepositoryInternal {
    Flux<Tag> findAllBy(Pageable pageable);

    @Override
    Mono<Tag> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Tag> findAllWithEagerRelationships();

    @Override
    Flux<Tag> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM tag entity WHERE entity.product_id = :id")
    Flux<Tag> findByProduct(Long id);

    @Query("SELECT * FROM tag entity WHERE entity.product_id IS NULL")
    Flux<Tag> findAllWhereProductIsNull();

    @Query("SELECT * FROM tag entity WHERE entity.project_id = :id")
    Flux<Tag> findByProject(Long id);

    @Query("SELECT * FROM tag entity WHERE entity.project_id IS NULL")
    Flux<Tag> findAllWhereProjectIsNull();

    @Query("SELECT * FROM tag entity WHERE entity.attachment_id = :id")
    Flux<Tag> findByAttachment(Long id);

    @Query("SELECT * FROM tag entity WHERE entity.attachment_id IS NULL")
    Flux<Tag> findAllWhereAttachmentIsNull();

    @Override
    <S extends Tag> Mono<S> save(S entity);

    @Override
    Flux<Tag> findAll();

    @Override
    Mono<Tag> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TagRepositoryInternal {
    <S extends Tag> Mono<S> save(S entity);

    Flux<Tag> findAllBy(Pageable pageable);

    Flux<Tag> findAll();

    Mono<Tag> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tag> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Tag> findOneWithEagerRelationships(Long id);

    Flux<Tag> findAllWithEagerRelationships();

    Flux<Tag> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
