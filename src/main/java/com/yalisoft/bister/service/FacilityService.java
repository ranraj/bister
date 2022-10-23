package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.repository.FacilityRepository;
import com.yalisoft.bister.repository.search.FacilitySearchRepository;
import com.yalisoft.bister.service.dto.FacilityDTO;
import com.yalisoft.bister.service.mapper.FacilityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Facility}.
 */
@Service
@Transactional
public class FacilityService {

    private final Logger log = LoggerFactory.getLogger(FacilityService.class);

    private final FacilityRepository facilityRepository;

    private final FacilityMapper facilityMapper;

    private final FacilitySearchRepository facilitySearchRepository;

    public FacilityService(
        FacilityRepository facilityRepository,
        FacilityMapper facilityMapper,
        FacilitySearchRepository facilitySearchRepository
    ) {
        this.facilityRepository = facilityRepository;
        this.facilityMapper = facilityMapper;
        this.facilitySearchRepository = facilitySearchRepository;
    }

    /**
     * Save a facility.
     *
     * @param facilityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FacilityDTO> save(FacilityDTO facilityDTO) {
        log.debug("Request to save Facility : {}", facilityDTO);
        return facilityRepository
            .save(facilityMapper.toEntity(facilityDTO))
            .flatMap(facilitySearchRepository::save)
            .map(facilityMapper::toDto);
    }

    /**
     * Update a facility.
     *
     * @param facilityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FacilityDTO> update(FacilityDTO facilityDTO) {
        log.debug("Request to update Facility : {}", facilityDTO);
        return facilityRepository
            .save(facilityMapper.toEntity(facilityDTO))
            .flatMap(facilitySearchRepository::save)
            .map(facilityMapper::toDto);
    }

    /**
     * Partially update a facility.
     *
     * @param facilityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FacilityDTO> partialUpdate(FacilityDTO facilityDTO) {
        log.debug("Request to partially update Facility : {}", facilityDTO);

        return facilityRepository
            .findById(facilityDTO.getId())
            .map(existingFacility -> {
                facilityMapper.partialUpdate(existingFacility, facilityDTO);

                return existingFacility;
            })
            .flatMap(facilityRepository::save)
            .flatMap(savedFacility -> {
                facilitySearchRepository.save(savedFacility);

                return Mono.just(savedFacility);
            })
            .map(facilityMapper::toDto);
    }

    /**
     * Get all the facilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FacilityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Facilities");
        return facilityRepository.findAllBy(pageable).map(facilityMapper::toDto);
    }

    /**
     * Get all the facilities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<FacilityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return facilityRepository.findAllWithEagerRelationships(pageable).map(facilityMapper::toDto);
    }

    /**
     * Returns the number of facilities available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return facilityRepository.count();
    }

    /**
     * Returns the number of facilities available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return facilitySearchRepository.count();
    }

    /**
     * Get one facility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FacilityDTO> findOne(Long id) {
        log.debug("Request to get Facility : {}", id);
        return facilityRepository.findOneWithEagerRelationships(id).map(facilityMapper::toDto);
    }

    /**
     * Delete the facility by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Facility : {}", id);
        return facilityRepository.deleteById(id).then(facilitySearchRepository.deleteById(id));
    }

    /**
     * Search for the facility corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FacilityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Facilities for query {}", query);
        return facilitySearchRepository.search(query, pageable).map(facilityMapper::toDto);
    }
}
