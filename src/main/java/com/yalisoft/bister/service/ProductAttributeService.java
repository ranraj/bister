package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductAttribute;
import com.yalisoft.bister.repository.ProductAttributeRepository;
import com.yalisoft.bister.repository.search.ProductAttributeSearchRepository;
import com.yalisoft.bister.service.dto.ProductAttributeDTO;
import com.yalisoft.bister.service.mapper.ProductAttributeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductAttribute}.
 */
@Service
@Transactional
public class ProductAttributeService {

    private final Logger log = LoggerFactory.getLogger(ProductAttributeService.class);

    private final ProductAttributeRepository productAttributeRepository;

    private final ProductAttributeMapper productAttributeMapper;

    private final ProductAttributeSearchRepository productAttributeSearchRepository;

    public ProductAttributeService(
        ProductAttributeRepository productAttributeRepository,
        ProductAttributeMapper productAttributeMapper,
        ProductAttributeSearchRepository productAttributeSearchRepository
    ) {
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeMapper = productAttributeMapper;
        this.productAttributeSearchRepository = productAttributeSearchRepository;
    }

    /**
     * Save a productAttribute.
     *
     * @param productAttributeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeDTO> save(ProductAttributeDTO productAttributeDTO) {
        log.debug("Request to save ProductAttribute : {}", productAttributeDTO);
        return productAttributeRepository
            .save(productAttributeMapper.toEntity(productAttributeDTO))
            .flatMap(productAttributeSearchRepository::save)
            .map(productAttributeMapper::toDto);
    }

    /**
     * Update a productAttribute.
     *
     * @param productAttributeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeDTO> update(ProductAttributeDTO productAttributeDTO) {
        log.debug("Request to update ProductAttribute : {}", productAttributeDTO);
        return productAttributeRepository
            .save(productAttributeMapper.toEntity(productAttributeDTO))
            .flatMap(productAttributeSearchRepository::save)
            .map(productAttributeMapper::toDto);
    }

    /**
     * Partially update a productAttribute.
     *
     * @param productAttributeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductAttributeDTO> partialUpdate(ProductAttributeDTO productAttributeDTO) {
        log.debug("Request to partially update ProductAttribute : {}", productAttributeDTO);

        return productAttributeRepository
            .findById(productAttributeDTO.getId())
            .map(existingProductAttribute -> {
                productAttributeMapper.partialUpdate(existingProductAttribute, productAttributeDTO);

                return existingProductAttribute;
            })
            .flatMap(productAttributeRepository::save)
            .flatMap(savedProductAttribute -> {
                productAttributeSearchRepository.save(savedProductAttribute);

                return Mono.just(savedProductAttribute);
            })
            .map(productAttributeMapper::toDto);
    }

    /**
     * Get all the productAttributes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductAttributeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductAttributes");
        return productAttributeRepository.findAllBy(pageable).map(productAttributeMapper::toDto);
    }

    /**
     * Returns the number of productAttributes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productAttributeRepository.count();
    }

    /**
     * Returns the number of productAttributes available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productAttributeSearchRepository.count();
    }

    /**
     * Get one productAttribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductAttributeDTO> findOne(Long id) {
        log.debug("Request to get ProductAttribute : {}", id);
        return productAttributeRepository.findById(id).map(productAttributeMapper::toDto);
    }

    /**
     * Delete the productAttribute by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductAttribute : {}", id);
        return productAttributeRepository.deleteById(id).then(productAttributeSearchRepository.deleteById(id));
    }

    /**
     * Search for the productAttribute corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductAttributeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductAttributes for query {}", query);
        return productAttributeSearchRepository.search(query, pageable).map(productAttributeMapper::toDto);
    }
}
