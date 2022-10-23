package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import com.yalisoft.bister.repository.ProductVariationAttributeTermRepository;
import com.yalisoft.bister.repository.search.ProductVariationAttributeTermSearchRepository;
import com.yalisoft.bister.service.dto.ProductVariationAttributeTermDTO;
import com.yalisoft.bister.service.mapper.ProductVariationAttributeTermMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductVariationAttributeTerm}.
 */
@Service
@Transactional
public class ProductVariationAttributeTermService {

    private final Logger log = LoggerFactory.getLogger(ProductVariationAttributeTermService.class);

    private final ProductVariationAttributeTermRepository productVariationAttributeTermRepository;

    private final ProductVariationAttributeTermMapper productVariationAttributeTermMapper;

    private final ProductVariationAttributeTermSearchRepository productVariationAttributeTermSearchRepository;

    public ProductVariationAttributeTermService(
        ProductVariationAttributeTermRepository productVariationAttributeTermRepository,
        ProductVariationAttributeTermMapper productVariationAttributeTermMapper,
        ProductVariationAttributeTermSearchRepository productVariationAttributeTermSearchRepository
    ) {
        this.productVariationAttributeTermRepository = productVariationAttributeTermRepository;
        this.productVariationAttributeTermMapper = productVariationAttributeTermMapper;
        this.productVariationAttributeTermSearchRepository = productVariationAttributeTermSearchRepository;
    }

    /**
     * Save a productVariationAttributeTerm.
     *
     * @param productVariationAttributeTermDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductVariationAttributeTermDTO> save(ProductVariationAttributeTermDTO productVariationAttributeTermDTO) {
        log.debug("Request to save ProductVariationAttributeTerm : {}", productVariationAttributeTermDTO);
        return productVariationAttributeTermRepository
            .save(productVariationAttributeTermMapper.toEntity(productVariationAttributeTermDTO))
            .flatMap(productVariationAttributeTermSearchRepository::save)
            .map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Update a productVariationAttributeTerm.
     *
     * @param productVariationAttributeTermDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductVariationAttributeTermDTO> update(ProductVariationAttributeTermDTO productVariationAttributeTermDTO) {
        log.debug("Request to update ProductVariationAttributeTerm : {}", productVariationAttributeTermDTO);
        return productVariationAttributeTermRepository
            .save(productVariationAttributeTermMapper.toEntity(productVariationAttributeTermDTO))
            .flatMap(productVariationAttributeTermSearchRepository::save)
            .map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Partially update a productVariationAttributeTerm.
     *
     * @param productVariationAttributeTermDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductVariationAttributeTermDTO> partialUpdate(ProductVariationAttributeTermDTO productVariationAttributeTermDTO) {
        log.debug("Request to partially update ProductVariationAttributeTerm : {}", productVariationAttributeTermDTO);

        return productVariationAttributeTermRepository
            .findById(productVariationAttributeTermDTO.getId())
            .map(existingProductVariationAttributeTerm -> {
                productVariationAttributeTermMapper.partialUpdate(existingProductVariationAttributeTerm, productVariationAttributeTermDTO);

                return existingProductVariationAttributeTerm;
            })
            .flatMap(productVariationAttributeTermRepository::save)
            .flatMap(savedProductVariationAttributeTerm -> {
                productVariationAttributeTermSearchRepository.save(savedProductVariationAttributeTerm);

                return Mono.just(savedProductVariationAttributeTerm);
            })
            .map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Get all the productVariationAttributeTerms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductVariationAttributeTermDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductVariationAttributeTerms");
        return productVariationAttributeTermRepository.findAllBy(pageable).map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Get all the productVariationAttributeTerms with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductVariationAttributeTermDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productVariationAttributeTermRepository
            .findAllWithEagerRelationships(pageable)
            .map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Returns the number of productVariationAttributeTerms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productVariationAttributeTermRepository.count();
    }

    /**
     * Returns the number of productVariationAttributeTerms available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productVariationAttributeTermSearchRepository.count();
    }

    /**
     * Get one productVariationAttributeTerm by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductVariationAttributeTermDTO> findOne(Long id) {
        log.debug("Request to get ProductVariationAttributeTerm : {}", id);
        return productVariationAttributeTermRepository.findOneWithEagerRelationships(id).map(productVariationAttributeTermMapper::toDto);
    }

    /**
     * Delete the productVariationAttributeTerm by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductVariationAttributeTerm : {}", id);
        return productVariationAttributeTermRepository.deleteById(id).then(productVariationAttributeTermSearchRepository.deleteById(id));
    }

    /**
     * Search for the productVariationAttributeTerm corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductVariationAttributeTermDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductVariationAttributeTerms for query {}", query);
        return productVariationAttributeTermSearchRepository.search(query, pageable).map(productVariationAttributeTermMapper::toDto);
    }
}
