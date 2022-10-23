package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Promotion;
import com.yalisoft.bister.repository.PromotionRepository;
import com.yalisoft.bister.repository.search.PromotionSearchRepository;
import com.yalisoft.bister.service.dto.PromotionDTO;
import com.yalisoft.bister.service.mapper.PromotionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Promotion}.
 */
@Service
@Transactional
public class PromotionService {

    private final Logger log = LoggerFactory.getLogger(PromotionService.class);

    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;

    private final PromotionSearchRepository promotionSearchRepository;

    public PromotionService(
        PromotionRepository promotionRepository,
        PromotionMapper promotionMapper,
        PromotionSearchRepository promotionSearchRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
        this.promotionSearchRepository = promotionSearchRepository;
    }

    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PromotionDTO> save(PromotionDTO promotionDTO) {
        log.debug("Request to save Promotion : {}", promotionDTO);
        return promotionRepository
            .save(promotionMapper.toEntity(promotionDTO))
            .flatMap(promotionSearchRepository::save)
            .map(promotionMapper::toDto);
    }

    /**
     * Update a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PromotionDTO> update(PromotionDTO promotionDTO) {
        log.debug("Request to update Promotion : {}", promotionDTO);
        return promotionRepository
            .save(promotionMapper.toEntity(promotionDTO))
            .flatMap(promotionSearchRepository::save)
            .map(promotionMapper::toDto);
    }

    /**
     * Partially update a promotion.
     *
     * @param promotionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PromotionDTO> partialUpdate(PromotionDTO promotionDTO) {
        log.debug("Request to partially update Promotion : {}", promotionDTO);

        return promotionRepository
            .findById(promotionDTO.getId())
            .map(existingPromotion -> {
                promotionMapper.partialUpdate(existingPromotion, promotionDTO);

                return existingPromotion;
            })
            .flatMap(promotionRepository::save)
            .flatMap(savedPromotion -> {
                promotionSearchRepository.save(savedPromotion);

                return Mono.just(savedPromotion);
            })
            .map(promotionMapper::toDto);
    }

    /**
     * Get all the promotions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PromotionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Promotions");
        return promotionRepository.findAllBy(pageable).map(promotionMapper::toDto);
    }

    /**
     * Returns the number of promotions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return promotionRepository.count();
    }

    /**
     * Returns the number of promotions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return promotionSearchRepository.count();
    }

    /**
     * Get one promotion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PromotionDTO> findOne(Long id) {
        log.debug("Request to get Promotion : {}", id);
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    /**
     * Delete the promotion by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Promotion : {}", id);
        return promotionRepository.deleteById(id).then(promotionSearchRepository.deleteById(id));
    }

    /**
     * Search for the promotion corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PromotionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Promotions for query {}", query);
        return promotionSearchRepository.search(query, pageable).map(promotionMapper::toDto);
    }
}
