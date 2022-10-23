package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProjectActivityRepository;
import com.yalisoft.bister.service.ProjectActivityService;
import com.yalisoft.bister.service.dto.ProjectActivityDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProjectActivity}.
 */
@RestController
@RequestMapping("/api")
public class ProjectActivityResource {

    private final Logger log = LoggerFactory.getLogger(ProjectActivityResource.class);

    private static final String ENTITY_NAME = "projectActivity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectActivityService projectActivityService;

    private final ProjectActivityRepository projectActivityRepository;

    public ProjectActivityResource(ProjectActivityService projectActivityService, ProjectActivityRepository projectActivityRepository) {
        this.projectActivityService = projectActivityService;
        this.projectActivityRepository = projectActivityRepository;
    }

    /**
     * {@code POST  /project-activities} : Create a new projectActivity.
     *
     * @param projectActivityDTO the projectActivityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectActivityDTO, or with status {@code 400 (Bad Request)} if the projectActivity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-activities")
    public Mono<ResponseEntity<ProjectActivityDTO>> createProjectActivity(@Valid @RequestBody ProjectActivityDTO projectActivityDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectActivity : {}", projectActivityDTO);
        if (projectActivityDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectActivity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectActivityService
            .save(projectActivityDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/project-activities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /project-activities/:id} : Updates an existing projectActivity.
     *
     * @param id the id of the projectActivityDTO to save.
     * @param projectActivityDTO the projectActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectActivityDTO,
     * or with status {@code 400 (Bad Request)} if the projectActivityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-activities/{id}")
    public Mono<ResponseEntity<ProjectActivityDTO>> updateProjectActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectActivityDTO projectActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectActivity : {}, {}", id, projectActivityDTO);
        if (projectActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectActivityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectActivityService
                    .update(projectActivityDTO)
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
     * {@code PATCH  /project-activities/:id} : Partial updates given fields of an existing projectActivity, field will ignore if it is null
     *
     * @param id the id of the projectActivityDTO to save.
     * @param projectActivityDTO the projectActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectActivityDTO,
     * or with status {@code 400 (Bad Request)} if the projectActivityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectActivityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-activities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectActivityDTO>> partialUpdateProjectActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectActivityDTO projectActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectActivity partially : {}, {}", id, projectActivityDTO);
        if (projectActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectActivityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectActivityDTO> result = projectActivityService.partialUpdate(projectActivityDTO);

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
     * {@code GET  /project-activities} : get all the projectActivities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectActivities in body.
     */
    @GetMapping("/project-activities")
    public Mono<ResponseEntity<List<ProjectActivityDTO>>> getAllProjectActivities(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProjectActivities");
        return projectActivityService
            .countAll()
            .zipWith(projectActivityService.findAll(pageable).collectList())
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
     * {@code GET  /project-activities/:id} : get the "id" projectActivity.
     *
     * @param id the id of the projectActivityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectActivityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-activities/{id}")
    public Mono<ResponseEntity<ProjectActivityDTO>> getProjectActivity(@PathVariable Long id) {
        log.debug("REST request to get ProjectActivity : {}", id);
        Mono<ProjectActivityDTO> projectActivityDTO = projectActivityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectActivityDTO);
    }

    /**
     * {@code DELETE  /project-activities/:id} : delete the "id" projectActivity.
     *
     * @param id the id of the projectActivityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-activities/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectActivity(@PathVariable Long id) {
        log.debug("REST request to delete ProjectActivity : {}", id);
        return projectActivityService
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
     * {@code SEARCH  /_search/project-activities?query=:query} : search for the projectActivity corresponding
     * to the query.
     *
     * @param query the query of the projectActivity search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-activities")
    public Mono<ResponseEntity<Flux<ProjectActivityDTO>>> searchProjectActivities(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProjectActivities for query {}", query);
        return projectActivityService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(projectActivityService.search(query, pageable)));
    }
}
