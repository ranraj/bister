package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.EnquiryResponse;
import com.yalisoft.bister.repository.EnquiryResponseRepository;
import com.yalisoft.bister.repository.search.EnquiryResponseSearchRepository;
import com.yalisoft.bister.service.dto.EnquiryResponseDTO;
import com.yalisoft.bister.service.mapper.EnquiryResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link EnquiryResponse}.
 */
@Service
@Transactional
public class EnquiryResponseService {

    private final Logger log = LoggerFactory.getLogger(EnquiryResponseService.class);

    private final EnquiryResponseRepository enquiryResponseRepository;

    private final EnquiryResponseMapper enquiryResponseMapper;

    private final EnquiryResponseSearchRepository enquiryResponseSearchRepository;

    public EnquiryResponseService(
        EnquiryResponseRepository enquiryResponseRepository,
        EnquiryResponseMapper enquiryResponseMapper,
        EnquiryResponseSearchRepository enquiryResponseSearchRepository
    ) {
        this.enquiryResponseRepository = enquiryResponseRepository;
        this.enquiryResponseMapper = enquiryResponseMapper;
        this.enquiryResponseSearchRepository = enquiryResponseSearchRepository;
    }

    /**
     * Save a enquiryResponse.
     *
     * @param enquiryResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnquiryResponseDTO> save(EnquiryResponseDTO enquiryResponseDTO) {
        log.debug("Request to save EnquiryResponse : {}", enquiryResponseDTO);
        return enquiryResponseRepository
            .save(enquiryResponseMapper.toEntity(enquiryResponseDTO))
            .flatMap(enquiryResponseSearchRepository::save)
            .map(enquiryResponseMapper::toDto);
    }

    /**
     * Update a enquiryResponse.
     *
     * @param enquiryResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnquiryResponseDTO> update(EnquiryResponseDTO enquiryResponseDTO) {
        log.debug("Request to update EnquiryResponse : {}", enquiryResponseDTO);
        return enquiryResponseRepository
            .save(enquiryResponseMapper.toEntity(enquiryResponseDTO))
            .flatMap(enquiryResponseSearchRepository::save)
            .map(enquiryResponseMapper::toDto);
    }

    /**
     * Partially update a enquiryResponse.
     *
     * @param enquiryResponseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<EnquiryResponseDTO> partialUpdate(EnquiryResponseDTO enquiryResponseDTO) {
        log.debug("Request to partially update EnquiryResponse : {}", enquiryResponseDTO);

        return enquiryResponseRepository
            .findById(enquiryResponseDTO.getId())
            .map(existingEnquiryResponse -> {
                enquiryResponseMapper.partialUpdate(existingEnquiryResponse, enquiryResponseDTO);

                return existingEnquiryResponse;
            })
            .flatMap(enquiryResponseRepository::save)
            .flatMap(savedEnquiryResponse -> {
                enquiryResponseSearchRepository.save(savedEnquiryResponse);

                return Mono.just(savedEnquiryResponse);
            })
            .map(enquiryResponseMapper::toDto);
    }

    /**
     * Get all the enquiryResponses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnquiryResponseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EnquiryResponses");
        return enquiryResponseRepository.findAllBy(pageable).map(enquiryResponseMapper::toDto);
    }

    /**
     * Get all the enquiryResponses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<EnquiryResponseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return enquiryResponseRepository.findAllWithEagerRelationships(pageable).map(enquiryResponseMapper::toDto);
    }

    /**
     * Returns the number of enquiryResponses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return enquiryResponseRepository.count();
    }

    /**
     * Returns the number of enquiryResponses available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return enquiryResponseSearchRepository.count();
    }

    /**
     * Get one enquiryResponse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<EnquiryResponseDTO> findOne(Long id) {
        log.debug("Request to get EnquiryResponse : {}", id);
        return enquiryResponseRepository.findOneWithEagerRelationships(id).map(enquiryResponseMapper::toDto);
    }

    /**
     * Delete the enquiryResponse by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete EnquiryResponse : {}", id);
        return enquiryResponseRepository.deleteById(id).then(enquiryResponseSearchRepository.deleteById(id));
    }

    /**
     * Search for the enquiryResponse corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnquiryResponseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of EnquiryResponses for query {}", query);
        return enquiryResponseSearchRepository.search(query, pageable).map(enquiryResponseMapper::toDto);
    }
}
