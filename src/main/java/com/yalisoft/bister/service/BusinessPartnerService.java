package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.BusinessPartner;
import com.yalisoft.bister.repository.BusinessPartnerRepository;
import com.yalisoft.bister.repository.search.BusinessPartnerSearchRepository;
import com.yalisoft.bister.service.dto.BusinessPartnerDTO;
import com.yalisoft.bister.service.mapper.BusinessPartnerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link BusinessPartner}.
 */
@Service
@Transactional
public class BusinessPartnerService {

    private final Logger log = LoggerFactory.getLogger(BusinessPartnerService.class);

    private final BusinessPartnerRepository businessPartnerRepository;

    private final BusinessPartnerMapper businessPartnerMapper;

    private final BusinessPartnerSearchRepository businessPartnerSearchRepository;

    public BusinessPartnerService(
        BusinessPartnerRepository businessPartnerRepository,
        BusinessPartnerMapper businessPartnerMapper,
        BusinessPartnerSearchRepository businessPartnerSearchRepository
    ) {
        this.businessPartnerRepository = businessPartnerRepository;
        this.businessPartnerMapper = businessPartnerMapper;
        this.businessPartnerSearchRepository = businessPartnerSearchRepository;
    }

    /**
     * Save a businessPartner.
     *
     * @param businessPartnerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BusinessPartnerDTO> save(BusinessPartnerDTO businessPartnerDTO) {
        log.debug("Request to save BusinessPartner : {}", businessPartnerDTO);
        return businessPartnerRepository
            .save(businessPartnerMapper.toEntity(businessPartnerDTO))
            .flatMap(businessPartnerSearchRepository::save)
            .map(businessPartnerMapper::toDto);
    }

    /**
     * Update a businessPartner.
     *
     * @param businessPartnerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BusinessPartnerDTO> update(BusinessPartnerDTO businessPartnerDTO) {
        log.debug("Request to update BusinessPartner : {}", businessPartnerDTO);
        return businessPartnerRepository
            .save(businessPartnerMapper.toEntity(businessPartnerDTO))
            .flatMap(businessPartnerSearchRepository::save)
            .map(businessPartnerMapper::toDto);
    }

    /**
     * Partially update a businessPartner.
     *
     * @param businessPartnerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BusinessPartnerDTO> partialUpdate(BusinessPartnerDTO businessPartnerDTO) {
        log.debug("Request to partially update BusinessPartner : {}", businessPartnerDTO);

        return businessPartnerRepository
            .findById(businessPartnerDTO.getId())
            .map(existingBusinessPartner -> {
                businessPartnerMapper.partialUpdate(existingBusinessPartner, businessPartnerDTO);

                return existingBusinessPartner;
            })
            .flatMap(businessPartnerRepository::save)
            .flatMap(savedBusinessPartner -> {
                businessPartnerSearchRepository.save(savedBusinessPartner);

                return Mono.just(savedBusinessPartner);
            })
            .map(businessPartnerMapper::toDto);
    }

    /**
     * Get all the businessPartners.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BusinessPartnerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BusinessPartners");
        return businessPartnerRepository.findAllBy(pageable).map(businessPartnerMapper::toDto);
    }

    /**
     * Returns the number of businessPartners available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return businessPartnerRepository.count();
    }

    /**
     * Returns the number of businessPartners available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return businessPartnerSearchRepository.count();
    }

    /**
     * Get one businessPartner by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BusinessPartnerDTO> findOne(Long id) {
        log.debug("Request to get BusinessPartner : {}", id);
        return businessPartnerRepository.findById(id).map(businessPartnerMapper::toDto);
    }

    /**
     * Delete the businessPartner by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete BusinessPartner : {}", id);
        return businessPartnerRepository.deleteById(id).then(businessPartnerSearchRepository.deleteById(id));
    }

    /**
     * Search for the businessPartner corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BusinessPartnerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BusinessPartners for query {}", query);
        return businessPartnerSearchRepository.search(query, pageable).map(businessPartnerMapper::toDto);
    }
}
