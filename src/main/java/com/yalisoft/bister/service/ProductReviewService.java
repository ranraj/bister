package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProductReview;
import com.yalisoft.bister.repository.ProductReviewRepository;
import com.yalisoft.bister.repository.search.ProductReviewSearchRepository;
import com.yalisoft.bister.service.dto.ProductReviewDTO;
import com.yalisoft.bister.service.mapper.ProductReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductReview}.
 */
@Service
@Transactional
public class ProductReviewService {

    private final Logger log = LoggerFactory.getLogger(ProductReviewService.class);

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewMapper productReviewMapper;

    private final ProductReviewSearchRepository productReviewSearchRepository;

    public ProductReviewService(
        ProductReviewRepository productReviewRepository,
        ProductReviewMapper productReviewMapper,
        ProductReviewSearchRepository productReviewSearchRepository
    ) {
        this.productReviewRepository = productReviewRepository;
        this.productReviewMapper = productReviewMapper;
        this.productReviewSearchRepository = productReviewSearchRepository;
    }

    /**
     * Save a productReview.
     *
     * @param productReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductReviewDTO> save(ProductReviewDTO productReviewDTO) {
        log.debug("Request to save ProductReview : {}", productReviewDTO);
        return productReviewRepository
            .save(productReviewMapper.toEntity(productReviewDTO))
            .flatMap(productReviewSearchRepository::save)
            .map(productReviewMapper::toDto);
    }

    /**
     * Update a productReview.
     *
     * @param productReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductReviewDTO> update(ProductReviewDTO productReviewDTO) {
        log.debug("Request to update ProductReview : {}", productReviewDTO);
        return productReviewRepository
            .save(productReviewMapper.toEntity(productReviewDTO))
            .flatMap(productReviewSearchRepository::save)
            .map(productReviewMapper::toDto);
    }

    /**
     * Partially update a productReview.
     *
     * @param productReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductReviewDTO> partialUpdate(ProductReviewDTO productReviewDTO) {
        log.debug("Request to partially update ProductReview : {}", productReviewDTO);

        return productReviewRepository
            .findById(productReviewDTO.getId())
            .map(existingProductReview -> {
                productReviewMapper.partialUpdate(existingProductReview, productReviewDTO);

                return existingProductReview;
            })
            .flatMap(productReviewRepository::save)
            .flatMap(savedProductReview -> {
                productReviewSearchRepository.save(savedProductReview);

                return Mono.just(savedProductReview);
            })
            .map(productReviewMapper::toDto);
    }

    /**
     * Get all the productReviews.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductReviewDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductReviews");
        return productReviewRepository.findAllBy(pageable).map(productReviewMapper::toDto);
    }

    /**
     * Get all the productReviews with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productReviewRepository.findAllWithEagerRelationships(pageable).map(productReviewMapper::toDto);
    }

    /**
     * Returns the number of productReviews available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productReviewRepository.count();
    }

    /**
     * Returns the number of productReviews available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productReviewSearchRepository.count();
    }

    /**
     * Get one productReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductReviewDTO> findOne(Long id) {
        log.debug("Request to get ProductReview : {}", id);
        return productReviewRepository.findOneWithEagerRelationships(id).map(productReviewMapper::toDto);
    }

    /**
     * Delete the productReview by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductReview : {}", id);
        return productReviewRepository.deleteById(id).then(productReviewSearchRepository.deleteById(id));
    }

    /**
     * Search for the productReview corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductReviewDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductReviews for query {}", query);
        return productReviewSearchRepository.search(query, pageable).map(productReviewMapper::toDto);
    }
}
