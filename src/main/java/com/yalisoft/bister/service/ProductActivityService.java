package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductActivity;
import com.yalisoft.bister.repository.ProductActivityRepository;
import com.yalisoft.bister.repository.search.ProductActivitySearchRepository;
import com.yalisoft.bister.service.dto.ProductActivityDTO;
import com.yalisoft.bister.service.mapper.ProductActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductActivity}.
 */
@Service
@Transactional
public class ProductActivityService {

    private final Logger log = LoggerFactory.getLogger(ProductActivityService.class);

    private final ProductActivityRepository productActivityRepository;

    private final ProductActivityMapper productActivityMapper;

    private final ProductActivitySearchRepository productActivitySearchRepository;

    public ProductActivityService(
        ProductActivityRepository productActivityRepository,
        ProductActivityMapper productActivityMapper,
        ProductActivitySearchRepository productActivitySearchRepository
    ) {
        this.productActivityRepository = productActivityRepository;
        this.productActivityMapper = productActivityMapper;
        this.productActivitySearchRepository = productActivitySearchRepository;
    }

    /**
     * Save a productActivity.
     *
     * @param productActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductActivityDTO> save(ProductActivityDTO productActivityDTO) {
        log.debug("Request to save ProductActivity : {}", productActivityDTO);
        return productActivityRepository
            .save(productActivityMapper.toEntity(productActivityDTO))
            .flatMap(productActivitySearchRepository::save)
            .map(productActivityMapper::toDto);
    }

    /**
     * Update a productActivity.
     *
     * @param productActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductActivityDTO> update(ProductActivityDTO productActivityDTO) {
        log.debug("Request to update ProductActivity : {}", productActivityDTO);
        return productActivityRepository
            .save(productActivityMapper.toEntity(productActivityDTO))
            .flatMap(productActivitySearchRepository::save)
            .map(productActivityMapper::toDto);
    }

    /**
     * Partially update a productActivity.
     *
     * @param productActivityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductActivityDTO> partialUpdate(ProductActivityDTO productActivityDTO) {
        log.debug("Request to partially update ProductActivity : {}", productActivityDTO);

        return productActivityRepository
            .findById(productActivityDTO.getId())
            .map(existingProductActivity -> {
                productActivityMapper.partialUpdate(existingProductActivity, productActivityDTO);

                return existingProductActivity;
            })
            .flatMap(productActivityRepository::save)
            .flatMap(savedProductActivity -> {
                productActivitySearchRepository.save(savedProductActivity);

                return Mono.just(savedProductActivity);
            })
            .map(productActivityMapper::toDto);
    }

    /**
     * Get all the productActivities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductActivities");
        return productActivityRepository.findAllBy(pageable).map(productActivityMapper::toDto);
    }

    /**
     * Get all the productActivities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductActivityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productActivityRepository.findAllWithEagerRelationships(pageable).map(productActivityMapper::toDto);
    }

    /**
     * Returns the number of productActivities available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productActivityRepository.count();
    }

    /**
     * Returns the number of productActivities available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productActivitySearchRepository.count();
    }

    /**
     * Get one productActivity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductActivityDTO> findOne(Long id) {
        log.debug("Request to get ProductActivity : {}", id);
        return productActivityRepository.findOneWithEagerRelationships(id).map(productActivityMapper::toDto);
    }

    /**
     * Delete the productActivity by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductActivity : {}", id);
        return productActivityRepository.deleteById(id).then(productActivitySearchRepository.deleteById(id));
    }

    /**
     * Search for the productActivity corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductActivityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductActivities for query {}", query);
        return productActivitySearchRepository.search(query, pageable).map(productActivityMapper::toDto);
    }
}
