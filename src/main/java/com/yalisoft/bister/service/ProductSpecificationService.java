package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductSpecification;
import com.yalisoft.bister.repository.ProductSpecificationRepository;
import com.yalisoft.bister.repository.search.ProductSpecificationSearchRepository;
import com.yalisoft.bister.service.dto.ProductSpecificationDTO;
import com.yalisoft.bister.service.mapper.ProductSpecificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductSpecification}.
 */
@Service
@Transactional
public class ProductSpecificationService {

    private final Logger log = LoggerFactory.getLogger(ProductSpecificationService.class);

    private final ProductSpecificationRepository productSpecificationRepository;

    private final ProductSpecificationMapper productSpecificationMapper;

    private final ProductSpecificationSearchRepository productSpecificationSearchRepository;

    public ProductSpecificationService(
        ProductSpecificationRepository productSpecificationRepository,
        ProductSpecificationMapper productSpecificationMapper,
        ProductSpecificationSearchRepository productSpecificationSearchRepository
    ) {
        this.productSpecificationRepository = productSpecificationRepository;
        this.productSpecificationMapper = productSpecificationMapper;
        this.productSpecificationSearchRepository = productSpecificationSearchRepository;
    }

    /**
     * Save a productSpecification.
     *
     * @param productSpecificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationDTO> save(ProductSpecificationDTO productSpecificationDTO) {
        log.debug("Request to save ProductSpecification : {}", productSpecificationDTO);
        return productSpecificationRepository
            .save(productSpecificationMapper.toEntity(productSpecificationDTO))
            .flatMap(productSpecificationSearchRepository::save)
            .map(productSpecificationMapper::toDto);
    }

    /**
     * Update a productSpecification.
     *
     * @param productSpecificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationDTO> update(ProductSpecificationDTO productSpecificationDTO) {
        log.debug("Request to update ProductSpecification : {}", productSpecificationDTO);
        return productSpecificationRepository
            .save(productSpecificationMapper.toEntity(productSpecificationDTO))
            .flatMap(productSpecificationSearchRepository::save)
            .map(productSpecificationMapper::toDto);
    }

    /**
     * Partially update a productSpecification.
     *
     * @param productSpecificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationDTO> partialUpdate(ProductSpecificationDTO productSpecificationDTO) {
        log.debug("Request to partially update ProductSpecification : {}", productSpecificationDTO);

        return productSpecificationRepository
            .findById(productSpecificationDTO.getId())
            .map(existingProductSpecification -> {
                productSpecificationMapper.partialUpdate(existingProductSpecification, productSpecificationDTO);

                return existingProductSpecification;
            })
            .flatMap(productSpecificationRepository::save)
            .flatMap(savedProductSpecification -> {
                productSpecificationSearchRepository.save(savedProductSpecification);

                return Mono.just(savedProductSpecification);
            })
            .map(productSpecificationMapper::toDto);
    }

    /**
     * Get all the productSpecifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductSpecificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductSpecifications");
        return productSpecificationRepository.findAllBy(pageable).map(productSpecificationMapper::toDto);
    }

    /**
     * Get all the productSpecifications with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductSpecificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productSpecificationRepository.findAllWithEagerRelationships(pageable).map(productSpecificationMapper::toDto);
    }

    /**
     * Returns the number of productSpecifications available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productSpecificationRepository.count();
    }

    /**
     * Returns the number of productSpecifications available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productSpecificationSearchRepository.count();
    }

    /**
     * Get one productSpecification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductSpecificationDTO> findOne(Long id) {
        log.debug("Request to get ProductSpecification : {}", id);
        return productSpecificationRepository.findOneWithEagerRelationships(id).map(productSpecificationMapper::toDto);
    }

    /**
     * Delete the productSpecification by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductSpecification : {}", id);
        return productSpecificationRepository.deleteById(id).then(productSpecificationSearchRepository.deleteById(id));
    }

    /**
     * Search for the productSpecification corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductSpecificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductSpecifications for query {}", query);
        return productSpecificationSearchRepository.search(query, pageable).map(productSpecificationMapper::toDto);
    }
}
