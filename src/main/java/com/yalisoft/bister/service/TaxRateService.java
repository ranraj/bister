package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.TaxRate;
import com.yalisoft.bister.repository.TaxRateRepository;
import com.yalisoft.bister.repository.search.TaxRateSearchRepository;
import com.yalisoft.bister.service.dto.TaxRateDTO;
import com.yalisoft.bister.service.mapper.TaxRateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TaxRate}.
 */
@Service
@Transactional
public class TaxRateService {

    private final Logger log = LoggerFactory.getLogger(TaxRateService.class);

    private final TaxRateRepository taxRateRepository;

    private final TaxRateMapper taxRateMapper;

    private final TaxRateSearchRepository taxRateSearchRepository;

    public TaxRateService(
        TaxRateRepository taxRateRepository,
        TaxRateMapper taxRateMapper,
        TaxRateSearchRepository taxRateSearchRepository
    ) {
        this.taxRateRepository = taxRateRepository;
        this.taxRateMapper = taxRateMapper;
        this.taxRateSearchRepository = taxRateSearchRepository;
    }

    /**
     * Save a taxRate.
     *
     * @param taxRateDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxRateDTO> save(TaxRateDTO taxRateDTO) {
        log.debug("Request to save TaxRate : {}", taxRateDTO);
        return taxRateRepository.save(taxRateMapper.toEntity(taxRateDTO)).flatMap(taxRateSearchRepository::save).map(taxRateMapper::toDto);
    }

    /**
     * Update a taxRate.
     *
     * @param taxRateDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxRateDTO> update(TaxRateDTO taxRateDTO) {
        log.debug("Request to update TaxRate : {}", taxRateDTO);
        return taxRateRepository.save(taxRateMapper.toEntity(taxRateDTO)).flatMap(taxRateSearchRepository::save).map(taxRateMapper::toDto);
    }

    /**
     * Partially update a taxRate.
     *
     * @param taxRateDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TaxRateDTO> partialUpdate(TaxRateDTO taxRateDTO) {
        log.debug("Request to partially update TaxRate : {}", taxRateDTO);

        return taxRateRepository
            .findById(taxRateDTO.getId())
            .map(existingTaxRate -> {
                taxRateMapper.partialUpdate(existingTaxRate, taxRateDTO);

                return existingTaxRate;
            })
            .flatMap(taxRateRepository::save)
            .flatMap(savedTaxRate -> {
                taxRateSearchRepository.save(savedTaxRate);

                return Mono.just(savedTaxRate);
            })
            .map(taxRateMapper::toDto);
    }

    /**
     * Get all the taxRates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TaxRateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaxRates");
        return taxRateRepository.findAllBy(pageable).map(taxRateMapper::toDto);
    }

    /**
     * Get all the taxRates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<TaxRateDTO> findAllWithEagerRelationships(Pageable pageable) {
        return taxRateRepository.findAllWithEagerRelationships(pageable).map(taxRateMapper::toDto);
    }

    /**
     * Returns the number of taxRates available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return taxRateRepository.count();
    }

    /**
     * Returns the number of taxRates available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return taxRateSearchRepository.count();
    }

    /**
     * Get one taxRate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TaxRateDTO> findOne(Long id) {
        log.debug("Request to get TaxRate : {}", id);
        return taxRateRepository.findOneWithEagerRelationships(id).map(taxRateMapper::toDto);
    }

    /**
     * Delete the taxRate by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TaxRate : {}", id);
        return taxRateRepository.deleteById(id).then(taxRateSearchRepository.deleteById(id));
    }

    /**
     * Search for the taxRate corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TaxRateDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TaxRates for query {}", query);
        return taxRateSearchRepository.search(query, pageable).map(taxRateMapper::toDto);
    }
}
