package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.repository.OrganisationRepository;
import com.yalisoft.bister.repository.search.OrganisationSearchRepository;
import com.yalisoft.bister.service.dto.OrganisationDTO;
import com.yalisoft.bister.service.mapper.OrganisationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Organisation}.
 */
@Service
@Transactional
public class OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationService.class);

    private final OrganisationRepository organisationRepository;

    private final OrganisationMapper organisationMapper;

    private final OrganisationSearchRepository organisationSearchRepository;

    public OrganisationService(
        OrganisationRepository organisationRepository,
        OrganisationMapper organisationMapper,
        OrganisationSearchRepository organisationSearchRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationMapper = organisationMapper;
        this.organisationSearchRepository = organisationSearchRepository;
    }

    /**
     * Save a organisation.
     *
     * @param organisationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrganisationDTO> save(OrganisationDTO organisationDTO) {
        log.debug("Request to save Organisation : {}", organisationDTO);
        return organisationRepository
            .save(organisationMapper.toEntity(organisationDTO))
            .flatMap(organisationSearchRepository::save)
            .map(organisationMapper::toDto);
    }

    /**
     * Update a organisation.
     *
     * @param organisationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrganisationDTO> update(OrganisationDTO organisationDTO) {
        log.debug("Request to update Organisation : {}", organisationDTO);
        return organisationRepository
            .save(organisationMapper.toEntity(organisationDTO))
            .flatMap(organisationSearchRepository::save)
            .map(organisationMapper::toDto);
    }

    /**
     * Partially update a organisation.
     *
     * @param organisationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrganisationDTO> partialUpdate(OrganisationDTO organisationDTO) {
        log.debug("Request to partially update Organisation : {}", organisationDTO);

        return organisationRepository
            .findById(organisationDTO.getId())
            .map(existingOrganisation -> {
                organisationMapper.partialUpdate(existingOrganisation, organisationDTO);

                return existingOrganisation;
            })
            .flatMap(organisationRepository::save)
            .flatMap(savedOrganisation -> {
                organisationSearchRepository.save(savedOrganisation);

                return Mono.just(savedOrganisation);
            })
            .map(organisationMapper::toDto);
    }

    /**
     * Get all the organisations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrganisationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Organisations");
        return organisationRepository.findAllBy(pageable).map(organisationMapper::toDto);
    }

    /**
     * Get all the organisations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OrganisationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return organisationRepository.findAllWithEagerRelationships(pageable).map(organisationMapper::toDto);
    }

    /**
     * Returns the number of organisations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return organisationRepository.count();
    }

    /**
     * Returns the number of organisations available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return organisationSearchRepository.count();
    }

    /**
     * Get one organisation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrganisationDTO> findOne(Long id) {
        log.debug("Request to get Organisation : {}", id);
        return organisationRepository.findOneWithEagerRelationships(id).map(organisationMapper::toDto);
    }

    /**
     * Delete the organisation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Organisation : {}", id);
        return organisationRepository.deleteById(id).then(organisationSearchRepository.deleteById(id));
    }

    /**
     * Search for the organisation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrganisationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Organisations for query {}", query);
        return organisationSearchRepository.search(query, pageable).map(organisationMapper::toDto);
    }
}
