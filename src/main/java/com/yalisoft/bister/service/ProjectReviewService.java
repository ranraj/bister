package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProjectReview;
import com.yalisoft.bister.repository.ProjectReviewRepository;
import com.yalisoft.bister.repository.search.ProjectReviewSearchRepository;
import com.yalisoft.bister.service.dto.ProjectReviewDTO;
import com.yalisoft.bister.service.mapper.ProjectReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProjectReview}.
 */
@Service
@Transactional
public class ProjectReviewService {

    private final Logger log = LoggerFactory.getLogger(ProjectReviewService.class);

    private final ProjectReviewRepository projectReviewRepository;

    private final ProjectReviewMapper projectReviewMapper;

    private final ProjectReviewSearchRepository projectReviewSearchRepository;

    public ProjectReviewService(
        ProjectReviewRepository projectReviewRepository,
        ProjectReviewMapper projectReviewMapper,
        ProjectReviewSearchRepository projectReviewSearchRepository
    ) {
        this.projectReviewRepository = projectReviewRepository;
        this.projectReviewMapper = projectReviewMapper;
        this.projectReviewSearchRepository = projectReviewSearchRepository;
    }

    /**
     * Save a projectReview.
     *
     * @param projectReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectReviewDTO> save(ProjectReviewDTO projectReviewDTO) {
        log.debug("Request to save ProjectReview : {}", projectReviewDTO);
        return projectReviewRepository
            .save(projectReviewMapper.toEntity(projectReviewDTO))
            .flatMap(projectReviewSearchRepository::save)
            .map(projectReviewMapper::toDto);
    }

    /**
     * Update a projectReview.
     *
     * @param projectReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectReviewDTO> update(ProjectReviewDTO projectReviewDTO) {
        log.debug("Request to update ProjectReview : {}", projectReviewDTO);
        return projectReviewRepository
            .save(projectReviewMapper.toEntity(projectReviewDTO))
            .flatMap(projectReviewSearchRepository::save)
            .map(projectReviewMapper::toDto);
    }

    /**
     * Partially update a projectReview.
     *
     * @param projectReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProjectReviewDTO> partialUpdate(ProjectReviewDTO projectReviewDTO) {
        log.debug("Request to partially update ProjectReview : {}", projectReviewDTO);

        return projectReviewRepository
            .findById(projectReviewDTO.getId())
            .map(existingProjectReview -> {
                projectReviewMapper.partialUpdate(existingProjectReview, projectReviewDTO);

                return existingProjectReview;
            })
            .flatMap(projectReviewRepository::save)
            .flatMap(savedProjectReview -> {
                projectReviewSearchRepository.save(savedProjectReview);

                return Mono.just(savedProjectReview);
            })
            .map(projectReviewMapper::toDto);
    }

    /**
     * Get all the projectReviews.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectReviewDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectReviews");
        return projectReviewRepository.findAllBy(pageable).map(projectReviewMapper::toDto);
    }

    /**
     * Get all the projectReviews with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProjectReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectReviewRepository.findAllWithEagerRelationships(pageable).map(projectReviewMapper::toDto);
    }

    /**
     * Returns the number of projectReviews available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return projectReviewRepository.count();
    }

    /**
     * Returns the number of projectReviews available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return projectReviewSearchRepository.count();
    }

    /**
     * Get one projectReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProjectReviewDTO> findOne(Long id) {
        log.debug("Request to get ProjectReview : {}", id);
        return projectReviewRepository.findOneWithEagerRelationships(id).map(projectReviewMapper::toDto);
    }

    /**
     * Delete the projectReview by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProjectReview : {}", id);
        return projectReviewRepository.deleteById(id).then(projectReviewSearchRepository.deleteById(id));
    }

    /**
     * Search for the projectReview corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectReviewDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectReviews for query {}", query);
        return projectReviewSearchRepository.search(query, pageable).map(projectReviewMapper::toDto);
    }
}
