package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductSpecificationGroup;
import com.yalisoft.bister.repository.ProductSpecificationGroupRepository;
import com.yalisoft.bister.repository.search.ProductSpecificationGroupSearchRepository;
import com.yalisoft.bister.service.dto.ProductSpecificationGroupDTO;
import com.yalisoft.bister.service.mapper.ProductSpecificationGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductSpecificationGroup}.
 */
@Service
@Transactional
public class ProductSpecificationGroupService {

    private final Logger log = LoggerFactory.getLogger(ProductSpecificationGroupService.class);

    private final ProductSpecificationGroupRepository productSpecificationGroupRepository;

    private final ProductSpecificationGroupMapper productSpecificationGroupMapper;

    private final ProductSpecificationGroupSearchRepository productSpecificationGroupSearchRepository;

    public ProductSpecificationGroupService(
        ProductSpecificationGroupRepository productSpecificationGroupRepository,
        ProductSpecificationGroupMapper productSpecificationGroupMapper,
        ProductSpecificationGroupSearchRepository productSpecificationGroupSearchRepository
    ) {
        this.productSpecificationGroupRepository = productSpecificationGroupRepository;
        this.productSpecificationGroupMapper = productSpecificationGroupMapper;
        this.productSpecificationGroupSearchRepository = productSpecificationGroupSearchRepository;
    }

    /**
     * Save a productSpecificationGroup.
     *
     * @param productSpecificationGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationGroupDTO> save(ProductSpecificationGroupDTO productSpecificationGroupDTO) {
        log.debug("Request to save ProductSpecificationGroup : {}", productSpecificationGroupDTO);
        return productSpecificationGroupRepository
            .save(productSpecificationGroupMapper.toEntity(productSpecificationGroupDTO))
            .flatMap(productSpecificationGroupSearchRepository::save)
            .map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Update a productSpecificationGroup.
     *
     * @param productSpecificationGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationGroupDTO> update(ProductSpecificationGroupDTO productSpecificationGroupDTO) {
        log.debug("Request to update ProductSpecificationGroup : {}", productSpecificationGroupDTO);
        return productSpecificationGroupRepository
            .save(productSpecificationGroupMapper.toEntity(productSpecificationGroupDTO))
            .flatMap(productSpecificationGroupSearchRepository::save)
            .map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Partially update a productSpecificationGroup.
     *
     * @param productSpecificationGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductSpecificationGroupDTO> partialUpdate(ProductSpecificationGroupDTO productSpecificationGroupDTO) {
        log.debug("Request to partially update ProductSpecificationGroup : {}", productSpecificationGroupDTO);

        return productSpecificationGroupRepository
            .findById(productSpecificationGroupDTO.getId())
            .map(existingProductSpecificationGroup -> {
                productSpecificationGroupMapper.partialUpdate(existingProductSpecificationGroup, productSpecificationGroupDTO);

                return existingProductSpecificationGroup;
            })
            .flatMap(productSpecificationGroupRepository::save)
            .flatMap(savedProductSpecificationGroup -> {
                productSpecificationGroupSearchRepository.save(savedProductSpecificationGroup);

                return Mono.just(savedProductSpecificationGroup);
            })
            .map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Get all the productSpecificationGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductSpecificationGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductSpecificationGroups");
        return productSpecificationGroupRepository.findAllBy(pageable).map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Get all the productSpecificationGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductSpecificationGroupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productSpecificationGroupRepository.findAllWithEagerRelationships(pageable).map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Returns the number of productSpecificationGroups available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productSpecificationGroupRepository.count();
    }

    /**
     * Returns the number of productSpecificationGroups available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productSpecificationGroupSearchRepository.count();
    }

    /**
     * Get one productSpecificationGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductSpecificationGroupDTO> findOne(Long id) {
        log.debug("Request to get ProductSpecificationGroup : {}", id);
        return productSpecificationGroupRepository.findOneWithEagerRelationships(id).map(productSpecificationGroupMapper::toDto);
    }

    /**
     * Delete the productSpecificationGroup by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductSpecificationGroup : {}", id);
        return productSpecificationGroupRepository.deleteById(id).then(productSpecificationGroupSearchRepository.deleteById(id));
    }

    /**
     * Search for the productSpecificationGroup corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductSpecificationGroupDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductSpecificationGroups for query {}", query);
        return productSpecificationGroupSearchRepository.search(query, pageable).map(productSpecificationGroupMapper::toDto);
    }
}
