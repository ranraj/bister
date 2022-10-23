package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.repository.TaxClassRepository;
import com.yalisoft.bister.repository.search.TaxClassSearchRepository;
import com.yalisoft.bister.service.dto.TaxClassDTO;
import com.yalisoft.bister.service.mapper.TaxClassMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TaxClass}.
 */
@Service
@Transactional
public class TaxClassService {

    private final Logger log = LoggerFactory.getLogger(TaxClassService.class);

    private final TaxClassRepository taxClassRepository;

    private final TaxClassMapper taxClassMapper;

    private final TaxClassSearchRepository taxClassSearchRepository;

    public TaxClassService(
        TaxClassRepository taxClassRepository,
        TaxClassMapper taxClassMapper,
        TaxClassSearchRepository taxClassSearchRepository
    ) {
        this.taxClassRepository = taxClassRepository;
        this.taxClassMapper = taxClassMapper;
        this.taxClassSearchRepository = taxClassSearchRepository;
    }

    /**
     * Save a taxClass.
     *
     * @param taxClassDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxClassDTO> save(TaxClassDTO taxClassDTO) {
        log.debug("Request to save TaxClass : {}", taxClassDTO);
        return taxClassRepository
            .save(taxClassMapper.toEntity(taxClassDTO))
            .flatMap(taxClassSearchRepository::save)
            .map(taxClassMapper::toDto);
    }

    /**
     * Update a taxClass.
     *
     * @param taxClassDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxClassDTO> update(TaxClassDTO taxClassDTO) {
        log.debug("Request to update TaxClass : {}", taxClassDTO);
        return taxClassRepository
            .save(taxClassMapper.toEntity(taxClassDTO))
            .flatMap(taxClassSearchRepository::save)
            .map(taxClassMapper::toDto);
    }

    /**
     * Partially update a taxClass.
     *
     * @param taxClassDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TaxClassDTO> partialUpdate(TaxClassDTO taxClassDTO) {
        log.debug("Request to partially update TaxClass : {}", taxClassDTO);

        return taxClassRepository
            .findById(taxClassDTO.getId())
            .map(existingTaxClass -> {
                taxClassMapper.partialUpdate(existingTaxClass, taxClassDTO);

                return existingTaxClass;
            })
            .flatMap(taxClassRepository::save)
            .flatMap(savedTaxClass -> {
                taxClassSearchRepository.save(savedTaxClass);

                return Mono.just(savedTaxClass);
            })
            .map(taxClassMapper::toDto);
    }

    /**
     * Get all the taxClasses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TaxClassDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaxClasses");
        return taxClassRepository.findAllBy(pageable).map(taxClassMapper::toDto);
    }

    /**
     * Returns the number of taxClasses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return taxClassRepository.count();
    }

    /**
     * Returns the number of taxClasses available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return taxClassSearchRepository.count();
    }

    /**
     * Get one taxClass by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TaxClassDTO> findOne(Long id) {
        log.debug("Request to get TaxClass : {}", id);
        return taxClassRepository.findById(id).map(taxClassMapper::toDto);
    }

    /**
     * Delete the taxClass by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TaxClass : {}", id);
        return taxClassRepository.deleteById(id).then(taxClassSearchRepository.deleteById(id));
    }

    /**
     * Search for the taxClass corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TaxClassDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TaxClasses for query {}", query);
        return taxClassSearchRepository.search(query, pageable).map(taxClassMapper::toDto);
    }
}
