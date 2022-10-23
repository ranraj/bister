package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.repository.AttachmentRepository;
import com.yalisoft.bister.repository.search.AttachmentSearchRepository;
import com.yalisoft.bister.service.dto.AttachmentDTO;
import com.yalisoft.bister.service.mapper.AttachmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Attachment}.
 */
@Service
@Transactional
public class AttachmentService {

    private final Logger log = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;

    private final AttachmentSearchRepository attachmentSearchRepository;

    public AttachmentService(
        AttachmentRepository attachmentRepository,
        AttachmentMapper attachmentMapper,
        AttachmentSearchRepository attachmentSearchRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        this.attachmentSearchRepository = attachmentSearchRepository;
    }

    /**
     * Save a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttachmentDTO> save(AttachmentDTO attachmentDTO) {
        log.debug("Request to save Attachment : {}", attachmentDTO);
        return attachmentRepository
            .save(attachmentMapper.toEntity(attachmentDTO))
            .flatMap(attachmentSearchRepository::save)
            .map(attachmentMapper::toDto);
    }

    /**
     * Update a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttachmentDTO> update(AttachmentDTO attachmentDTO) {
        log.debug("Request to update Attachment : {}", attachmentDTO);
        return attachmentRepository
            .save(attachmentMapper.toEntity(attachmentDTO))
            .flatMap(attachmentSearchRepository::save)
            .map(attachmentMapper::toDto);
    }

    /**
     * Partially update a attachment.
     *
     * @param attachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO) {
        log.debug("Request to partially update Attachment : {}", attachmentDTO);

        return attachmentRepository
            .findById(attachmentDTO.getId())
            .map(existingAttachment -> {
                attachmentMapper.partialUpdate(existingAttachment, attachmentDTO);

                return existingAttachment;
            })
            .flatMap(attachmentRepository::save)
            .flatMap(savedAttachment -> {
                attachmentSearchRepository.save(savedAttachment);

                return Mono.just(savedAttachment);
            })
            .map(attachmentMapper::toDto);
    }

    /**
     * Get all the attachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AttachmentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Attachments");
        return attachmentRepository.findAllBy(pageable).map(attachmentMapper::toDto);
    }

    /**
     * Get all the attachments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<AttachmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return attachmentRepository.findAllWithEagerRelationships(pageable).map(attachmentMapper::toDto);
    }

    /**
     * Returns the number of attachments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return attachmentRepository.count();
    }

    /**
     * Returns the number of attachments available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return attachmentSearchRepository.count();
    }

    /**
     * Get one attachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AttachmentDTO> findOne(Long id) {
        log.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findOneWithEagerRelationships(id).map(attachmentMapper::toDto);
    }

    /**
     * Delete the attachment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Attachment : {}", id);
        return attachmentRepository.deleteById(id).then(attachmentSearchRepository.deleteById(id));
    }

    /**
     * Search for the attachment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AttachmentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Attachments for query {}", query);
        return attachmentSearchRepository.search(query, pageable).map(attachmentMapper::toDto);
    }
}
