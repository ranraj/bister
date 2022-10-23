package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Certification;
import com.yalisoft.bister.repository.CertificationRepository;
import com.yalisoft.bister.repository.search.CertificationSearchRepository;
import com.yalisoft.bister.service.dto.CertificationDTO;
import com.yalisoft.bister.service.mapper.CertificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Certification}.
 */
@Service
@Transactional
public class CertificationService {

    private final Logger log = LoggerFactory.getLogger(CertificationService.class);

    private final CertificationRepository certificationRepository;

    private final CertificationMapper certificationMapper;

    private final CertificationSearchRepository certificationSearchRepository;

    public CertificationService(
        CertificationRepository certificationRepository,
        CertificationMapper certificationMapper,
        CertificationSearchRepository certificationSearchRepository
    ) {
        this.certificationRepository = certificationRepository;
        this.certificationMapper = certificationMapper;
        this.certificationSearchRepository = certificationSearchRepository;
    }

    /**
     * Save a certification.
     *
     * @param certificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CertificationDTO> save(CertificationDTO certificationDTO) {
        log.debug("Request to save Certification : {}", certificationDTO);
        return certificationRepository
            .save(certificationMapper.toEntity(certificationDTO))
            .flatMap(certificationSearchRepository::save)
            .map(certificationMapper::toDto);
    }

    /**
     * Update a certification.
     *
     * @param certificationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CertificationDTO> update(CertificationDTO certificationDTO) {
        log.debug("Request to update Certification : {}", certificationDTO);
        return certificationRepository
            .save(certificationMapper.toEntity(certificationDTO))
            .flatMap(certificationSearchRepository::save)
            .map(certificationMapper::toDto);
    }

    /**
     * Partially update a certification.
     *
     * @param certificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CertificationDTO> partialUpdate(CertificationDTO certificationDTO) {
        log.debug("Request to partially update Certification : {}", certificationDTO);

        return certificationRepository
            .findById(certificationDTO.getId())
            .map(existingCertification -> {
                certificationMapper.partialUpdate(existingCertification, certificationDTO);

                return existingCertification;
            })
            .flatMap(certificationRepository::save)
            .flatMap(savedCertification -> {
                certificationSearchRepository.save(savedCertification);

                return Mono.just(savedCertification);
            })
            .map(certificationMapper::toDto);
    }

    /**
     * Get all the certifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CertificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Certifications");
        return certificationRepository.findAllBy(pageable).map(certificationMapper::toDto);
    }

    /**
     * Returns the number of certifications available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return certificationRepository.count();
    }

    /**
     * Returns the number of certifications available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return certificationSearchRepository.count();
    }

    /**
     * Get one certification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CertificationDTO> findOne(Long id) {
        log.debug("Request to get Certification : {}", id);
        return certificationRepository.findById(id).map(certificationMapper::toDto);
    }

    /**
     * Delete the certification by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Certification : {}", id);
        return certificationRepository.deleteById(id).then(certificationSearchRepository.deleteById(id));
    }

    /**
     * Search for the certification corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CertificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Certifications for query {}", query);
        return certificationSearchRepository.search(query, pageable).map(certificationMapper::toDto);
    }
}
