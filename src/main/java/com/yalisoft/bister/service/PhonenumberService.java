package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Phonenumber;
import com.yalisoft.bister.repository.PhonenumberRepository;
import com.yalisoft.bister.repository.search.PhonenumberSearchRepository;
import com.yalisoft.bister.service.dto.PhonenumberDTO;
import com.yalisoft.bister.service.mapper.PhonenumberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Phonenumber}.
 */
@Service
@Transactional
public class PhonenumberService {

    private final Logger log = LoggerFactory.getLogger(PhonenumberService.class);

    private final PhonenumberRepository phonenumberRepository;

    private final PhonenumberMapper phonenumberMapper;

    private final PhonenumberSearchRepository phonenumberSearchRepository;

    public PhonenumberService(
        PhonenumberRepository phonenumberRepository,
        PhonenumberMapper phonenumberMapper,
        PhonenumberSearchRepository phonenumberSearchRepository
    ) {
        this.phonenumberRepository = phonenumberRepository;
        this.phonenumberMapper = phonenumberMapper;
        this.phonenumberSearchRepository = phonenumberSearchRepository;
    }

    /**
     * Save a phonenumber.
     *
     * @param phonenumberDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PhonenumberDTO> save(PhonenumberDTO phonenumberDTO) {
        log.debug("Request to save Phonenumber : {}", phonenumberDTO);
        return phonenumberRepository
            .save(phonenumberMapper.toEntity(phonenumberDTO))
            .flatMap(phonenumberSearchRepository::save)
            .map(phonenumberMapper::toDto);
    }

    /**
     * Update a phonenumber.
     *
     * @param phonenumberDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PhonenumberDTO> update(PhonenumberDTO phonenumberDTO) {
        log.debug("Request to update Phonenumber : {}", phonenumberDTO);
        return phonenumberRepository
            .save(phonenumberMapper.toEntity(phonenumberDTO))
            .flatMap(phonenumberSearchRepository::save)
            .map(phonenumberMapper::toDto);
    }

    /**
     * Partially update a phonenumber.
     *
     * @param phonenumberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PhonenumberDTO> partialUpdate(PhonenumberDTO phonenumberDTO) {
        log.debug("Request to partially update Phonenumber : {}", phonenumberDTO);

        return phonenumberRepository
            .findById(phonenumberDTO.getId())
            .map(existingPhonenumber -> {
                phonenumberMapper.partialUpdate(existingPhonenumber, phonenumberDTO);

                return existingPhonenumber;
            })
            .flatMap(phonenumberRepository::save)
            .flatMap(savedPhonenumber -> {
                phonenumberSearchRepository.save(savedPhonenumber);

                return Mono.just(savedPhonenumber);
            })
            .map(phonenumberMapper::toDto);
    }

    /**
     * Get all the phonenumbers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PhonenumberDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Phonenumbers");
        return phonenumberRepository.findAllBy(pageable).map(phonenumberMapper::toDto);
    }

    /**
     * Get all the phonenumbers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<PhonenumberDTO> findAllWithEagerRelationships(Pageable pageable) {
        return phonenumberRepository.findAllWithEagerRelationships(pageable).map(phonenumberMapper::toDto);
    }

    /**
     * Returns the number of phonenumbers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return phonenumberRepository.count();
    }

    /**
     * Returns the number of phonenumbers available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return phonenumberSearchRepository.count();
    }

    /**
     * Get one phonenumber by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PhonenumberDTO> findOne(Long id) {
        log.debug("Request to get Phonenumber : {}", id);
        return phonenumberRepository.findOneWithEagerRelationships(id).map(phonenumberMapper::toDto);
    }

    /**
     * Delete the phonenumber by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Phonenumber : {}", id);
        return phonenumberRepository.deleteById(id).then(phonenumberSearchRepository.deleteById(id));
    }

    /**
     * Search for the phonenumber corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PhonenumberDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Phonenumbers for query {}", query);
        return phonenumberSearchRepository.search(query, pageable).map(phonenumberMapper::toDto);
    }
}
