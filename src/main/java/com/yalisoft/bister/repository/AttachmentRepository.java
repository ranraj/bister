package com.yalisoft.bister.repository;

import com.yalisoft.bister.domain.Attachment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Attachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends ReactiveCrudRepository<Attachment, Long>, AttachmentRepositoryInternal {
    Flux<Attachment> findAllBy(Pageable pageable);

    @Override
    Mono<Attachment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Attachment> findAllWithEagerRelationships();

    @Override
    Flux<Attachment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM attachment entity WHERE entity.product_id = :id")
    Flux<Attachment> findByProduct(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.product_id IS NULL")
    Flux<Attachment> findAllWhereProductIsNull();

    @Query("SELECT * FROM attachment entity WHERE entity.project_id = :id")
    Flux<Attachment> findByProject(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.project_id IS NULL")
    Flux<Attachment> findAllWhereProjectIsNull();

    @Query("SELECT * FROM attachment entity WHERE entity.enquiry_id = :id")
    Flux<Attachment> findByEnquiry(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.enquiry_id IS NULL")
    Flux<Attachment> findAllWhereEnquiryIsNull();

    @Query("SELECT * FROM attachment entity WHERE entity.certification_id = :id")
    Flux<Attachment> findByCertification(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.certification_id IS NULL")
    Flux<Attachment> findAllWhereCertificationIsNull();

    @Query("SELECT * FROM attachment entity WHERE entity.product_specification_id = :id")
    Flux<Attachment> findByProductSpecification(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.product_specification_id IS NULL")
    Flux<Attachment> findAllWhereProductSpecificationIsNull();

    @Query("SELECT * FROM attachment entity WHERE entity.project_specification_id = :id")
    Flux<Attachment> findByProjectSpecification(Long id);

    @Query("SELECT * FROM attachment entity WHERE entity.project_specification_id IS NULL")
    Flux<Attachment> findAllWhereProjectSpecificationIsNull();

    @Override
    <S extends Attachment> Mono<S> save(S entity);

    @Override
    Flux<Attachment> findAll();

    @Override
    Mono<Attachment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AttachmentRepositoryInternal {
    <S extends Attachment> Mono<S> save(S entity);

    Flux<Attachment> findAllBy(Pageable pageable);

    Flux<Attachment> findAll();

    Mono<Attachment> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Attachment> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Attachment> findOneWithEagerRelationships(Long id);

    Flux<Attachment> findAllWithEagerRelationships();

    Flux<Attachment> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
