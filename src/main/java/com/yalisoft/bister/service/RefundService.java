package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Refund;
import com.yalisoft.bister.repository.RefundRepository;
import com.yalisoft.bister.repository.search.RefundSearchRepository;
import com.yalisoft.bister.service.dto.RefundDTO;
import com.yalisoft.bister.service.mapper.RefundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Refund}.
 */
@Service
@Transactional
public class RefundService {

    private final Logger log = LoggerFactory.getLogger(RefundService.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    private final RefundSearchRepository refundSearchRepository;

    public RefundService(RefundRepository refundRepository, RefundMapper refundMapper, RefundSearchRepository refundSearchRepository) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
        this.refundSearchRepository = refundSearchRepository;
    }

    /**
     * Save a refund.
     *
     * @param refundDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RefundDTO> save(RefundDTO refundDTO) {
        log.debug("Request to save Refund : {}", refundDTO);
        return refundRepository.save(refundMapper.toEntity(refundDTO)).flatMap(refundSearchRepository::save).map(refundMapper::toDto);
    }

    /**
     * Update a refund.
     *
     * @param refundDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RefundDTO> update(RefundDTO refundDTO) {
        log.debug("Request to update Refund : {}", refundDTO);
        return refundRepository.save(refundMapper.toEntity(refundDTO)).flatMap(refundSearchRepository::save).map(refundMapper::toDto);
    }

    /**
     * Partially update a refund.
     *
     * @param refundDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RefundDTO> partialUpdate(RefundDTO refundDTO) {
        log.debug("Request to partially update Refund : {}", refundDTO);

        return refundRepository
            .findById(refundDTO.getId())
            .map(existingRefund -> {
                refundMapper.partialUpdate(existingRefund, refundDTO);

                return existingRefund;
            })
            .flatMap(refundRepository::save)
            .flatMap(savedRefund -> {
                refundSearchRepository.save(savedRefund);

                return Mono.just(savedRefund);
            })
            .map(refundMapper::toDto);
    }

    /**
     * Get all the refunds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RefundDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Refunds");
        return refundRepository.findAllBy(pageable).map(refundMapper::toDto);
    }

    /**
     * Get all the refunds with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<RefundDTO> findAllWithEagerRelationships(Pageable pageable) {
        return refundRepository.findAllWithEagerRelationships(pageable).map(refundMapper::toDto);
    }

    /**
     * Returns the number of refunds available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return refundRepository.count();
    }

    /**
     * Returns the number of refunds available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return refundSearchRepository.count();
    }

    /**
     * Get one refund by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RefundDTO> findOne(Long id) {
        log.debug("Request to get Refund : {}", id);
        return refundRepository.findOneWithEagerRelationships(id).map(refundMapper::toDto);
    }

    /**
     * Delete the refund by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Refund : {}", id);
        return refundRepository.deleteById(id).then(refundSearchRepository.deleteById(id));
    }

    /**
     * Search for the refund corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RefundDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Refunds for query {}", query);
        return refundSearchRepository.search(query, pageable).map(refundMapper::toDto);
    }
}
