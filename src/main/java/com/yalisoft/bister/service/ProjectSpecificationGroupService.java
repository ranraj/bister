package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.repository.ProjectSpecificationGroupRepository;
import com.yalisoft.bister.repository.search.ProjectSpecificationGroupSearchRepository;
import com.yalisoft.bister.service.dto.ProjectSpecificationGroupDTO;
import com.yalisoft.bister.service.mapper.ProjectSpecificationGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProjectSpecificationGroup}.
 */
@Service
@Transactional
public class ProjectSpecificationGroupService {

    private final Logger log = LoggerFactory.getLogger(ProjectSpecificationGroupService.class);

    private final ProjectSpecificationGroupRepository projectSpecificationGroupRepository;

    private final ProjectSpecificationGroupMapper projectSpecificationGroupMapper;

    private final ProjectSpecificationGroupSearchRepository projectSpecificationGroupSearchRepository;

    public ProjectSpecificationGroupService(
        ProjectSpecificationGroupRepository projectSpecificationGroupRepository,
        ProjectSpecificationGroupMapper projectSpecificationGroupMapper,
        ProjectSpecificationGroupSearchRepository projectSpecificationGroupSearchRepository
    ) {
        this.projectSpecificationGroupRepository = projectSpecificationGroupRepository;
        this.projectSpecificationGroupMapper = projectSpecificationGroupMapper;
        this.projectSpecificationGroupSearchRepository = projectSpecificationGroupSearchRepository;
    }

    /**
     * Save a projectSpecificationGroup.
     *
     * @param projectSpecificationGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationGroupDTO> save(ProjectSpecificationGroupDTO projectSpecificationGroupDTO) {
        log.debug("Request to save ProjectSpecificationGroup : {}", projectSpecificationGroupDTO);
        return projectSpecificationGroupRepository
            .save(projectSpecificationGroupMapper.toEntity(projectSpecificationGroupDTO))
            .flatMap(projectSpecificationGroupSearchRepository::save)
            .map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Update a projectSpecificationGroup.
     *
     * @param projectSpecificationGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationGroupDTO> update(ProjectSpecificationGroupDTO projectSpecificationGroupDTO) {
        log.debug("Request to update ProjectSpecificationGroup : {}", projectSpecificationGroupDTO);
        return projectSpecificationGroupRepository
            .save(projectSpecificationGroupMapper.toEntity(projectSpecificationGroupDTO))
            .flatMap(projectSpecificationGroupSearchRepository::save)
            .map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Partially update a projectSpecificationGroup.
     *
     * @param projectSpecificationGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProjectSpecificationGroupDTO> partialUpdate(ProjectSpecificationGroupDTO projectSpecificationGroupDTO) {
        log.debug("Request to partially update ProjectSpecificationGroup : {}", projectSpecificationGroupDTO);

        return projectSpecificationGroupRepository
            .findById(projectSpecificationGroupDTO.getId())
            .map(existingProjectSpecificationGroup -> {
                projectSpecificationGroupMapper.partialUpdate(existingProjectSpecificationGroup, projectSpecificationGroupDTO);

                return existingProjectSpecificationGroup;
            })
            .flatMap(projectSpecificationGroupRepository::save)
            .flatMap(savedProjectSpecificationGroup -> {
                projectSpecificationGroupSearchRepository.save(savedProjectSpecificationGroup);

                return Mono.just(savedProjectSpecificationGroup);
            })
            .map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Get all the projectSpecificationGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectSpecificationGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectSpecificationGroups");
        return projectSpecificationGroupRepository.findAllBy(pageable).map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Get all the projectSpecificationGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProjectSpecificationGroupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectSpecificationGroupRepository.findAllWithEagerRelationships(pageable).map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Returns the number of projectSpecificationGroups available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return projectSpecificationGroupRepository.count();
    }

    /**
     * Returns the number of projectSpecificationGroups available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return projectSpecificationGroupSearchRepository.count();
    }

    /**
     * Get one projectSpecificationGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProjectSpecificationGroupDTO> findOne(Long id) {
        log.debug("Request to get ProjectSpecificationGroup : {}", id);
        return projectSpecificationGroupRepository.findOneWithEagerRelationships(id).map(projectSpecificationGroupMapper::toDto);
    }

    /**
     * Delete the projectSpecificationGroup by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProjectSpecificationGroup : {}", id);
        return projectSpecificationGroupRepository.deleteById(id).then(projectSpecificationGroupSearchRepository.deleteById(id));
    }

    /**
     * Search for the projectSpecificationGroup corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProjectSpecificationGroupDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectSpecificationGroups for query {}", query);
        return projectSpecificationGroupSearchRepository.search(query, pageable).map(projectSpecificationGroupMapper::toDto);
    }
}
