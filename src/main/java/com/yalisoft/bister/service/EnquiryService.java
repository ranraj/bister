package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.repository.EnquiryRepository;
import com.yalisoft.bister.repository.search.EnquirySearchRepository;
import com.yalisoft.bister.service.dto.EnquiryDTO;
import com.yalisoft.bister.service.mapper.EnquiryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Enquiry}.
 */
@Service
@Transactional
public class EnquiryService {

    private final Logger log = LoggerFactory.getLogger(EnquiryService.class);

    private final EnquiryRepository enquiryRepository;

    private final EnquiryMapper enquiryMapper;

    private final EnquirySearchRepository enquirySearchRepository;

    public EnquiryService(
        EnquiryRepository enquiryRepository,
        EnquiryMapper enquiryMapper,
        EnquirySearchRepository enquirySearchRepository
    ) {
        this.enquiryRepository = enquiryRepository;
        this.enquiryMapper = enquiryMapper;
        this.enquirySearchRepository = enquirySearchRepository;
    }

    /**
     * Save a enquiry.
     *
     * @param enquiryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnquiryDTO> save(EnquiryDTO enquiryDTO) {
        log.debug("Request to save Enquiry : {}", enquiryDTO);
        return enquiryRepository.save(enquiryMapper.toEntity(enquiryDTO)).flatMap(enquirySearchRepository::save).map(enquiryMapper::toDto);
    }

    /**
     * Update a enquiry.
     *
     * @param enquiryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnquiryDTO> update(EnquiryDTO enquiryDTO) {
        log.debug("Request to update Enquiry : {}", enquiryDTO);
        return enquiryRepository.save(enquiryMapper.toEntity(enquiryDTO)).flatMap(enquirySearchRepository::save).map(enquiryMapper::toDto);
    }

    /**
     * Partially update a enquiry.
     *
     * @param enquiryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<EnquiryDTO> partialUpdate(EnquiryDTO enquiryDTO) {
        log.debug("Request to partially update Enquiry : {}", enquiryDTO);

        return enquiryRepository
            .findById(enquiryDTO.getId())
            .map(existingEnquiry -> {
                enquiryMapper.partialUpdate(existingEnquiry, enquiryDTO);

                return existingEnquiry;
            })
            .flatMap(enquiryRepository::save)
            .flatMap(savedEnquiry -> {
                enquirySearchRepository.save(savedEnquiry);

                return Mono.just(savedEnquiry);
            })
            .map(enquiryMapper::toDto);
    }

    /**
     * Get all the enquiries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnquiryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Enquiries");
        return enquiryRepository.findAllBy(pageable).map(enquiryMapper::toDto);
    }

    /**
     * Get all the enquiries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<EnquiryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return enquiryRepository.findAllWithEagerRelationships(pageable).map(enquiryMapper::toDto);
    }

    /**
     * Returns the number of enquiries available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return enquiryRepository.count();
    }

    /**
     * Returns the number of enquiries available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return enquirySearchRepository.count();
    }

    /**
     * Get one enquiry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<EnquiryDTO> findOne(Long id) {
        log.debug("Request to get Enquiry : {}", id);
        return enquiryRepository.findOneWithEagerRelationships(id).map(enquiryMapper::toDto);
    }

    /**
     * Delete the enquiry by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Enquiry : {}", id);
        return enquiryRepository.deleteById(id).then(enquirySearchRepository.deleteById(id));
    }

    /**
     * Search for the enquiry corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnquiryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Enquiries for query {}", query);
        return enquirySearchRepository.search(query, pageable).map(enquiryMapper::toDto);
    }
}
