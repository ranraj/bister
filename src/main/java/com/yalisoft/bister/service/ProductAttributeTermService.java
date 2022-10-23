package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductAttributeTerm;
import com.yalisoft.bister.repository.ProductAttributeTermRepository;
import com.yalisoft.bister.repository.search.ProductAttributeTermSearchRepository;
import com.yalisoft.bister.service.dto.ProductAttributeTermDTO;
import com.yalisoft.bister.service.mapper.ProductAttributeTermMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductAttributeTerm}.
 */
@Service
@Transactional
public class ProductAttributeTermService {

    private final Logger log = LoggerFactory.getLogger(ProductAttributeTermService.class);

    private final ProductAttributeTermRepository productAttributeTermRepository;

    private final ProductAttributeTermMapper productAttributeTermMapper;

    private final ProductAttributeTermSearchRepository productAttributeTermSearchRepository;

    public ProductAttributeTermService(
        ProductAttributeTermRepository productAttributeTermRepository,
        ProductAttributeTermMapper productAttributeTermMapper,
        ProductAttributeTermSearchRepository productAttributeTermSearchRepository
    ) {
        this.productAttributeTermRepository = productAttributeTermRepository;
        this.productAttributeTermMapper = productAttributeTermMapper;
        this.productAttributeTermSearchRepository = productAttributeTermSearchRepository;
    }

    /**
     * Save a productAttributeTerm.
     *
     * @param productAttributeTermDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeTermDTO> save(ProductAttributeTermDTO productAttributeTermDTO) {
        log.debug("Request to save ProductAttributeTerm : {}", productAttributeTermDTO);
        return productAttributeTermRepository
            .save(productAttributeTermMapper.toEntity(productAttributeTermDTO))
            .flatMap(productAttributeTermSearchRepository::save)
            .map(productAttributeTermMapper::toDto);
    }

    /**
     * Update a productAttributeTerm.
     *
     * @param productAttributeTermDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeTermDTO> update(ProductAttributeTermDTO productAttributeTermDTO) {
        log.debug("Request to update ProductAttributeTerm : {}", productAttributeTermDTO);
        return productAttributeTermRepository
            .save(productAttributeTermMapper.toEntity(productAttributeTermDTO))
            .flatMap(productAttributeTermSearchRepository::save)
            .map(productAttributeTermMapper::toDto);
    }

    /**
     * Partially update a productAttributeTerm.
     *
     * @param productAttributeTermDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeTermDTO> partialUpdate(ProductAttributeTermDTO productAttributeTermDTO) {
        log.debug("Request to partially update ProductAttributeTerm : {}", productAttributeTermDTO);

        return productAttributeTermRepository
            .findById(productAttributeTermDTO.getId())
            .map(existingProductAttributeTerm -> {
                productAttributeTermMapper.partialUpdate(existingProductAttributeTerm, productAttributeTermDTO);

                return existingProductAttributeTerm;
            })
            .flatMap(productAttributeTermRepository::save)
            .flatMap(savedProductAttributeTerm -> {
                productAttributeTermSearchRepository.save(savedProductAttributeTerm);

                return Mono.just(savedProductAttributeTerm);
            })
            .map(productAttributeTermMapper::toDto);
    }

    /**
     * Get all the productAttributeTerms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductAttributeTermDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductAttributeTerms");
        return productAttributeTermRepository.findAllBy(pageable).map(productAttributeTermMapper::toDto);
    }

    /**
     * Get all the productAttributeTerms with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductAttributeTermDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productAttributeTermRepository.findAllWithEagerRelationships(pageable).map(productAttributeTermMapper::toDto);
    }

    /**
     * Returns the number of productAttributeTerms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productAttributeTermRepository.count();
    }

    /**
     * Returns the number of productAttributeTerms available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productAttributeTermSearchRepository.count();
    }

    /**
     * Get one productAttributeTerm by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductAttributeTermDTO> findOne(Long id) {
        log.debug("Request to get ProductAttributeTerm : {}", id);
        return productAttributeTermRepository.findOneWithEagerRelationships(id).map(productAttributeTermMapper::toDto);
    }

    /**
     * Delete the productAttributeTerm by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductAttributeTerm : {}", id);
        return productAttributeTermRepository.deleteById(id).then(productAttributeTermSearchRepository.deleteById(id));
    }

    /**
     * Search for the productAttributeTerm corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductAttributeTermDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductAttributeTerms for query {}", query);
        return productAttributeTermSearchRepository.search(query, pageable).map(productAttributeTermMapper::toDto);
    }
}
