package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProjectSpecificationRepository;
import com.yalisoft.bister.service.ProjectSpecificationService;
import com.yalisoft.bister.service.dto.ProjectSpecificationDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProjectSpecification}.
 */
@RestController
@RequestMapping("/api")
public class ProjectSpecificationResource {

    private final Logger log = LoggerFactory.getLogger(ProjectSpecificationResource.class);

    private static final String ENTITY_NAME = "projectSpecification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectSpecificationService projectSpecificationService;

    private final ProjectSpecificationRepository projectSpecificationRepository;

    public ProjectSpecificationResource(
        ProjectSpecificationService projectSpecificationService,
        ProjectSpecificationRepository projectSpecificationRepository
    ) {
        this.projectSpecificationService = projectSpecificationService;
        this.projectSpecificationRepository = projectSpecificationRepository;
    }

    /**
     * {@code POST  /project-specifications} : Create a new projectSpecification.
     *
     * @param projectSpecificationDTO the projectSpecificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectSpecificationDTO, or with status {@code 400 (Bad Request)} if the projectSpecification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-specifications")
    public Mono<ResponseEntity<ProjectSpecificationDTO>> createProjectSpecification(
        @Valid @RequestBody ProjectSpecificationDTO projectSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProjectSpecification : {}", projectSpecificationDTO);
        if (projectSpecificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectSpecification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectSpecificationService
            .save(projectSpecificationDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/project-specifications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /project-specifications/:id} : Updates an existing projectSpecification.
     *
     * @param id the id of the projectSpecificationDTO to save.
     * @param projectSpecificationDTO the projectSpecificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSpecificationDTO,
     * or with status {@code 400 (Bad Request)} if the projectSpecificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectSpecificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-specifications/{id}")
    public Mono<ResponseEntity<ProjectSpecificationDTO>> updateProjectSpecification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectSpecificationDTO projectSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectSpecification : {}, {}", id, projectSpecificationDTO);
        if (projectSpecificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSpecificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSpecificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectSpecificationService
                    .update(projectSpecificationDTO)
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
     * {@code PATCH  /project-specifications/:id} : Partial updates given fields of an existing projectSpecification, field will ignore if it is null
     *
     * @param id the id of the projectSpecificationDTO to save.
     * @param projectSpecificationDTO the projectSpecificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSpecificationDTO,
     * or with status {@code 400 (Bad Request)} if the projectSpecificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectSpecificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectSpecificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-specifications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectSpecificationDTO>> partialUpdateProjectSpecification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectSpecificationDTO projectSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectSpecification partially : {}, {}", id, projectSpecificationDTO);
        if (projectSpecificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSpecificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSpecificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectSpecificationDTO> result = projectSpecificationService.partialUpdate(projectSpecificationDTO);

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
     * {@code GET  /project-specifications} : get all the projectSpecifications.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectSpecifications in body.
     */
    @GetMapping("/project-specifications")
    public Mono<ResponseEntity<List<ProjectSpecificationDTO>>> getAllProjectSpecifications(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProjectSpecifications");
        return projectSpecificationService
            .countAll()
            .zipWith(projectSpecificationService.findAll(pageable).collectList())
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
     * {@code GET  /project-specifications/:id} : get the "id" projectSpecification.
     *
     * @param id the id of the projectSpecificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectSpecificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-specifications/{id}")
    public Mono<ResponseEntity<ProjectSpecificationDTO>> getProjectSpecification(@PathVariable Long id) {
        log.debug("REST request to get ProjectSpecification : {}", id);
        Mono<ProjectSpecificationDTO> projectSpecificationDTO = projectSpecificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectSpecificationDTO);
    }

    /**
     * {@code DELETE  /project-specifications/:id} : delete the "id" projectSpecification.
     *
     * @param id the id of the projectSpecificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-specifications/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectSpecification(@PathVariable Long id) {
        log.debug("REST request to delete ProjectSpecification : {}", id);
        return projectSpecificationService
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
     * {@code SEARCH  /_search/project-specifications?query=:query} : search for the projectSpecification corresponding
     * to the query.
     *
     * @param query the query of the projectSpecification search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-specifications")
    public Mono<ResponseEntity<Flux<ProjectSpecificationDTO>>> searchProjectSpecifications(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProjectSpecifications for query {}", query);
        return projectSpecificationService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(projectSpecificationService.search(query, pageable)));
    }
}
