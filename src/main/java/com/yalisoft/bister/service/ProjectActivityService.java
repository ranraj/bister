package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProjectActivity;
import com.yalisoft.bister.repository.ProjectActivityRepository;
import com.yalisoft.bister.repository.search.ProjectActivitySearchRepository;
import com.yalisoft.bister.service.dto.ProjectActivityDTO;
import com.yalisoft.bister.service.mapper.ProjectActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProjectActivity}.
 */
@Service
@Transactional
public class ProjectActivityService {

    private final Logger log = LoggerFactory.getLogger(ProjectActivityService.class);

    private final ProjectActivityRepository projectActivityRepository;

    private final ProjectActivityMapper projectActivityMapper;

    private final ProjectActivitySearchRepository projectActivitySearchRepository;

    public ProjectActivityService(
        ProjectActivityRepository projectActivityRepository,
        ProjectActivityMapper projectActivityMapper,
        ProjectActivitySearchRepository projectActivitySearchRepository
    ) {
        this.projectActivityRepository = projectActivityRepository;
        this.projectActivityMapper = projectActivityMapper;
        this.projectActivitySearchRepository = projectActivitySearchRepository;
    }

    /**
     * Save a projectActivity.
     *
     * @param projectActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectActivityDTO> save(ProjectActivityDTO projectActivityDTO) {
        log.debug("Request to save ProjectActivity : {}", projectActivityDTO);
        return projectActivityRepository
            .save(projectActivityMapper.toEntity(projectActivityDTO))
            .flatMap(projectActivitySearchRepository::save)
            .map(projectActivityMapper::toDto);
    }

    /**
     * Update a projectActivity.
     *
     * @param projectActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectActivityDTO> update(ProjectActivityDTO projectActivityDTO) {
        log.debug("Request to update ProjectActivity : {}", projectActivityDTO);
        return projectActivityRepository
            .save(projectActivityMapper.toEntity(projectActivityDTO))
            .flatMap(projectActivitySearchRepository::save)
            .map(projectActivityMapper::toDto);
    }

    /**
     * Partially update a projectActivity.
     *
     * @param projectActivityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProjectActivityDTO> partialUpdate(ProjectActivityDTO projectActivityDTO) {
        log.debug("Request to partially update ProjectActivity : {}", projectActivityDTO);

        return projectActivityRepository
            .findById(projectActivityDTO.getId())
            .map(existingProjectActivity -> {
                projectActivityMapper.partialUpdate(existingProjectActivity, projectActivityDTO);

                return existingProjectActivity;
            })
            .flatMap(projectActivityRepository::save)
            .flatMap(savedProjectActivity -> {
                projectActivitySearchRepository.save(savedProjectActivity);

                return Mono.just(savedProjectActivity);
            })
            .map(projectActivityMapper::toDto);
    }

    /**
     * Get all the projectActivities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectActivities");
        return projectActivityRepository.findAllBy(pageable).map(projectActivityMapper::toDto);
    }

    /**
     * Get all the projectActivities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProjectActivityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectActivityRepository.findAllWithEagerRelationships(pageable).map(projectActivityMapper::toDto);
    }

    /**
     * Returns the number of projectActivities available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return projectActivityRepository.count();
    }

    /**
     * Returns the number of projectActivities available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return projectActivitySearchRepository.count();
    }

    /**
     * Get one projectActivity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProjectActivityDTO> findOne(Long id) {
        log.debug("Request to get ProjectActivity : {}", id);
        return projectActivityRepository.findOneWithEagerRelationships(id).map(projectActivityMapper::toDto);
    }

    /**
     * Delete the projectActivity by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProjectActivity : {}", id);
        return projectActivityRepository.deleteById(id).then(projectActivitySearchRepository.deleteById(id));
    }

    /**
     * Search for the projectActivity corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectActivityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectActivities for query {}", query);
        return projectActivitySearchRepository.search(query, pageable).map(projectActivityMapper::toDto);
    }
}
