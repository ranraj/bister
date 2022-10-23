package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProjectSpecification;
import com.yalisoft.bister.repository.ProjectSpecificationRepository;
import com.yalisoft.bister.repository.search.ProjectSpecificationSearchRepository;
import com.yalisoft.bister.service.dto.ProjectSpecificationDTO;
import com.yalisoft.bister.service.mapper.ProjectSpecificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProjectSpecification}.
 */
@Service
@Transactional
public class ProjectSpecificationService {

    private final Logger log = LoggerFactory.getLogger(ProjectSpecificationService.class);

    private final ProjectSpecificationRepository projectSpecificationRepository;

    private final ProjectSpecificationMapper projectSpecificationMapper;

    private final ProjectSpecificationSearchRepository projectSpecificationSearchRepository;

    public ProjectSpecificationService(
        ProjectSpecificationRepository projectSpecificationRepository,
        ProjectSpecificationMapper projectSpecificationMapper,
        ProjectSpecificationSearchRepository projectSpecificationSearchRepository
    ) {
        this.projectSpecificationRepository = projectSpecificationRepository;
        this.projectSpecificationMapper = projectSpecificationMapper;
        this.projectSpecificationSearchRepository = projectSpecificationSearchRepository;
    }

    /**
     * Save a projectSpecification.
     *
     * @param projectSpecificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationDTO> save(ProjectSpecificationDTO projectSpecificationDTO) {
        log.debug("Request to save ProjectSpecification : {}", projectSpecificationDTO);
        return projectSpecificationRepository
            .save(projectSpecificationMapper.toEntity(projectSpecificationDTO))
            .flatMap(projectSpecificationSearchRepository::save)
            .map(projectSpecificationMapper::toDto);
    }

    /**
     * Update a projectSpecification.
     *
     * @param projectSpecificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationDTO> update(ProjectSpecificationDTO projectSpecificationDTO) {
        log.debug("Request to update ProjectSpecification : {}", projectSpecificationDTO);
        return projectSpecificationRepository
            .save(projectSpecificationMapper.toEntity(projectSpecificationDTO))
            .flatMap(projectSpecificationSearchRepository::save)
            .map(projectSpecificationMapper::toDto);
    }

    /**
     * Partially update a projectSpecification.
     *
     * @param projectSpecificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationDTO> partialUpdate(ProjectSpecificationDTO projectSpecificationDTO) {
        log.debug("Request to partially update ProjectSpecification : {}", projectSpecificationDTO);

        return projectSpecificationRepository
            .findById(projectSpecificationDTO.getId())
            .map(existingProjectSpecification -> {
                projectSpecificationMapper.partialUpdate(existingProjectSpecification, projectSpecificationDTO);

                return existingProjectSpecification;
            })
            .flatMap(projectSpecificationRepository::save)
            .flatMap(savedProjectSpecification -> {
                projectSpecificationSearchRepository.save(savedProjectSpecification);

                return Mono.just(savedProjectSpecification);
            })
            .map(projectSpecificationMapper::toDto);
    }

    /**
     * Get all the projectSpecifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectSpecificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectSpecifications");
        return projectSpecificationRepository.findAllBy(pageable).map(projectSpecificationMapper::toDto);
    }

    /**
     * Get all the projectSpecifications with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProjectSpecificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectSpecificationRepository.findAllWithEagerRelationships(pageable).map(projectSpecificationMapper::toDto);
    }

    /**
     * Returns the number of projectSpecifications available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return projectSpecificationRepository.count();
    }

    /**
     * Returns the number of projectSpecifications available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return projectSpecificationSearchRepository.count();
    }

    /**
     * Get one projectSpecification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProjectSpecificationDTO> findOne(Long id) {
        log.debug("Request to get ProjectSpecification : {}", id);
        return projectSpecificationRepository.findOneWithEagerRelationships(id).map(projectSpecificationMapper::toDto);
    }

    /**
     * Delete the projectSpecification by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProjectSpecification : {}", id);
        return projectSpecificationRepository.deleteById(id).then(projectSpecificationSearchRepository.deleteById(id));
    }

    /**
     * Search for the projectSpecification corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectSpecificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectSpecifications for query {}", query);
        return projectSpecificationSearchRepository.search(query, pageable).map(projectSpecificationMapper::toDto);
    }
}
