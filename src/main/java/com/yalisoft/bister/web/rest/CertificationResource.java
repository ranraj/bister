package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.CertificationRepository;
import com.yalisoft.bister.service.CertificationService;
import com.yalisoft.bister.service.dto.CertificationDTO;
import com.yalisoft.bister.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.yalisoft.bister.domain.Certification}.
 */
@RestController
@RequestMapping("/api")
public class CertificationResource {

    private final Logger log = LoggerFactory.getLogger(CertificationResource.class);

    private static final String ENTITY_NAME = "certification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificationService certificationService;

    private final CertificationRepository certificationRepository;

    public CertificationResource(CertificationService certificationService, CertificationRepository certificationRepository) {
        this.certificationService = certificationService;
        this.certificationRepository = certificationRepository;
    }

    /**
     * {@code POST  /certifications} : Create a new certification.
     *
     * @param certificationDTO the certificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificationDTO, or with status {@code 400 (Bad Request)} if the certification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/certifications")
    public Mono<ResponseEntity<CertificationDTO>> createCertification(@Valid @RequestBody CertificationDTO certificationDTO)
        throws URISyntaxException {
        log.debug("REST request to save Certification : {}", certificationDTO);
        if (certificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new certification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return certificationService
            .save(certificationDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/certifications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /certifications/:id} : Updates an existing certification.
     *
     * @param id the id of the certificationDTO to save.
     * @param certificationDTO the certificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificationDTO,
     * or with status {@code 400 (Bad Request)} if the certificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/certifications/{id}")
    public Mono<ResponseEntity<CertificationDTO>> updateCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CertificationDTO certificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Certification : {}, {}", id, certificationDTO);
        if (certificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return certificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return certificationService
                    .update(certificationDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /certifications/:id} : Partial updates given fields of an existing certification, field will ignore if it is null
     *
     * @param id the id of the certificationDTO to save.
     * @param certificationDTO the certificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificationDTO,
     * or with status {@code 400 (Bad Request)} if the certificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the certificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/certifications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CertificationDTO>> partialUpdateCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CertificationDTO certificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Certification partially : {}, {}", id, certificationDTO);
        if (certificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return certificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CertificationDTO> result = certificationService.partialUpdate(certificationDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /certifications} : get all the certifications.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certifications in body.
     */
    @GetMapping("/certifications")
    public Mono<ResponseEntity<List<CertificationDTO>>> getAllCertifications(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Certifications");
        return certificationService
            .countAll()
            .zipWith(certificationService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /certifications/:id} : get the "id" certification.
     *
     * @param id the id of the certificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/certifications/{id}")
    public Mono<ResponseEntity<CertificationDTO>> getCertification(@PathVariable Long id) {
        log.debug("REST request to get Certification : {}", id);
        Mono<CertificationDTO> certificationDTO = certificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certificationDTO);
    }

    /**
     * {@code DELETE  /certifications/:id} : delete the "id" certification.
     *
     * @param id the id of the certificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/certifications/{id}")
    public Mono<ResponseEntity<Void>> deleteCertification(@PathVariable Long id) {
        log.debug("REST request to delete Certification : {}", id);
        return certificationService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /_search/certifications?query=:query} : search for the certification corresponding
     * to the query.
     *
     * @param query the query of the certification search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/certifications")
    public Mono<ResponseEntity<Flux<CertificationDTO>>> searchCertifications(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Certifications for query {}", query);
        return certificationService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(certificationService.search(query, pageable)));
    }
}
