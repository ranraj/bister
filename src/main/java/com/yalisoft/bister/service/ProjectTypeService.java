package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProjectType;
import com.yalisoft.bister.repository.ProjectTypeRepository;
import com.yalisoft.bister.repository.search.ProjectTypeSearchRepository;
import com.yalisoft.bister.service.dto.ProjectTypeDTO;
import com.yalisoft.bister.service.mapper.ProjectTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProjectType}.
 */
@Service
@Transactional
public class ProjectTypeService {

    private final Logger log = LoggerFactory.getLogger(ProjectTypeService.class);

    private final ProjectTypeRepository projectTypeRepository;

    private final ProjectTypeMapper projectTypeMapper;

    private final ProjectTypeSearchRepository projectTypeSearchRepository;

    public ProjectTypeService(
        ProjectTypeRepository projectTypeRepository,
        ProjectTypeMapper projectTypeMapper,
        ProjectTypeSearchRepository projectTypeSearchRepository
    ) {
        this.projectTypeRepository = projectTypeRepository;
        this.projectTypeMapper = projectTypeMapper;
        this.projectTypeSearchRepository = projectTypeSearchRepository;
    }

    /**
     * Save a projectType.
     *
     * @param projectTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectTypeDTO> save(ProjectTypeDTO projectTypeDTO) {
        log.debug("Request to save ProjectType : {}", projectTypeDTO);
        return projectTypeRepository
            .save(projectTypeMapper.toEntity(projectTypeDTO))
            .flatMap(projectTypeSearchRepository::save)
            .map(projectTypeMapper::toDto);
    }

    /**
     * Update a projectType.
     *
     * @param projectTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectTypeDTO> update(ProjectTypeDTO projectTypeDTO) {
        log.debug("Request to update ProjectType : {}", projectTypeDTO);
        return projectTypeRepository
            .save(projectTypeMapper.toEntity(projectTypeDTO))
            .flatMap(projectTypeSearchRepository::save)
            .map(projectTypeMapper::toDto);
    }

    /**
     * Partially update a projectType.
     *
     * @param projectTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProjectTypeDTO> partialUpdate(ProjectTypeDTO projectTypeDTO) {
        log.debug("Request to partially update ProjectType : {}", projectTypeDTO);

        return projectTypeRepository
            .findById(projectTypeDTO.getId())
            .map(existingProjectType -> {
                projectTypeMapper.partialUpdate(existingProjectType, projectTypeDTO);

                return existingProjectType;
            })
            .flatMap(projectTypeRepository::save)
            .flatMap(savedProjectType -> {
                projectTypeSearchRepository.save(savedProjectType);

                return Mono.just(savedProjectType);
            })
            .map(projectTypeMapper::toDto);
    }

    /**
     * Get all the projectTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectTypes");
        return projectTypeRepository.findAllBy(pageable).map(projectTypeMapper::toDto);
    }

    /**
     * Returns the number of projectTypes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return projectTypeRepository.count();
    }

    /**
     * Returns the number of projectTypes available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return projectTypeSearchRepository.count();
    }

    /**
     * Get one projectType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProjectTypeDTO> findOne(Long id) {
        log.debug("Request to get ProjectType : {}", id);
        return projectTypeRepository.findById(id).map(projectTypeMapper::toDto);
    }

    /**
     * Delete the projectType by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProjectType : {}", id);
        return projectTypeRepository.deleteById(id).then(projectTypeSearchRepository.deleteById(id));
    }

    /**
     * Search for the projectType corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectTypes for query {}", query);
        return projectTypeSearchRepository.search(query, pageable).map(projectTypeMapper::toDto);
    }
}
