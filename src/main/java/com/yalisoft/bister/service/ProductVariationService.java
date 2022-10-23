package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.repository.ProductVariationRepository;
import com.yalisoft.bister.repository.search.ProductVariationSearchRepository;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import com.yalisoft.bister.service.mapper.ProductVariationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductVariation}.
 */
@Service
@Transactional
public class ProductVariationService {

    private final Logger log = LoggerFactory.getLogger(ProductVariationService.class);

    private final ProductVariationRepository productVariationRepository;

    private final ProductVariationMapper productVariationMapper;

    private final ProductVariationSearchRepository productVariationSearchRepository;

    public ProductVariationService(
        ProductVariationRepository productVariationRepository,
        ProductVariationMapper productVariationMapper,
        ProductVariationSearchRepository productVariationSearchRepository
    ) {
        this.productVariationRepository = productVariationRepository;
        this.productVariationMapper = productVariationMapper;
        this.productVariationSearchRepository = productVariationSearchRepository;
    }

    /**
     * Save a productVariation.
     *
     * @param productVariationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductVariationDTO> save(ProductVariationDTO productVariationDTO) {
        log.debug("Request to save ProductVariation : {}", productVariationDTO);
        return productVariationRepository
            .save(productVariationMapper.toEntity(productVariationDTO))
            .flatMap(productVariationSearchRepository::save)
            .map(productVariationMapper::toDto);
    }

    /**
     * Update a productVariation.
     *
     * @param productVariationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductVariationDTO> update(ProductVariationDTO productVariationDTO) {
        log.debug("Request to update ProductVariation : {}", productVariationDTO);
        return productVariationRepository
            .save(productVariationMapper.toEntity(productVariationDTO))
            .flatMap(productVariationSearchRepository::save)
            .map(productVariationMapper::toDto);
    }

    /**
     * Partially update a productVariation.
     *
     * @param productVariationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductVariationDTO> partialUpdate(ProductVariationDTO productVariationDTO) {
        log.debug("Request to partially update ProductVariation : {}", productVariationDTO);

        return productVariationRepository
            .findById(productVariationDTO.getId())
            .map(existingProductVariation -> {
                productVariationMapper.partialUpdate(existingProductVariation, productVariationDTO);

                return existingProductVariation;
            })
            .flatMap(productVariationRepository::save)
            .flatMap(savedProductVariation -> {
                productVariationSearchRepository.save(savedProductVariation);

                return Mono.just(savedProductVariation);
            })
            .map(productVariationMapper::toDto);
    }

    /**
     * Get all the productVariations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductVariationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductVariations");
        return productVariationRepository.findAllBy(pageable).map(productVariationMapper::toDto);
    }

    /**
     * Get all the productVariations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductVariationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productVariationRepository.findAllWithEagerRelationships(pageable).map(productVariationMapper::toDto);
    }

    /**
     * Returns the number of productVariations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productVariationRepository.count();
    }

    /**
     * Returns the number of productVariations available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productVariationSearchRepository.count();
    }

    /**
     * Get one productVariation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductVariationDTO> findOne(Long id) {
        log.debug("Request to get ProductVariation : {}", id);
        return productVariationRepository.findOneWithEagerRelationships(id).map(productVariationMapper::toDto);
    }

    /**
     * Delete the productVariation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductVariation : {}", id);
        return productVariationRepository.deleteById(id).then(productVariationSearchRepository.deleteById(id));
    }

    /**
     * Search for the productVariation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductVariationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductVariations for query {}", query);
        return productVariationSearchRepository.search(query, pageable).map(productVariationMapper::toDto);
    }
}
