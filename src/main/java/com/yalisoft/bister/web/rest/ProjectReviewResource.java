package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProjectReviewRepository;
import com.yalisoft.bister.service.ProjectReviewService;
import com.yalisoft.bister.service.dto.ProjectReviewDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProjectReview}.
 */
@RestController
@RequestMapping("/api")
public class ProjectReviewResource {

    private final Logger log = LoggerFactory.getLogger(ProjectReviewResource.class);

    private static final String ENTITY_NAME = "projectReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectReviewService projectReviewService;

    private final ProjectReviewRepository projectReviewRepository;

    public ProjectReviewResource(ProjectReviewService projectReviewService, ProjectReviewRepository projectReviewRepository) {
        this.projectReviewService = projectReviewService;
        this.projectReviewRepository = projectReviewRepository;
    }

    /**
     * {@code POST  /project-reviews} : Create a new projectReview.
     *
     * @param projectReviewDTO the projectReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectReviewDTO, or with status {@code 400 (Bad Request)} if the projectReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-reviews")
    public Mono<ResponseEntity<ProjectReviewDTO>> createProjectReview(@Valid @RequestBody ProjectReviewDTO projectReviewDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectReview : {}", projectReviewDTO);
        if (projectReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectReviewService
            .save(projectReviewDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/project-reviews/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /project-reviews/:id} : Updates an existing projectReview.
     *
     * @param id the id of the projectReviewDTO to save.
     * @param projectReviewDTO the projectReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectReviewDTO,
     * or with status {@code 400 (Bad Request)} if the projectReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-reviews/{id}")
    public Mono<ResponseEntity<ProjectReviewDTO>> updateProjectReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectReviewDTO projectReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectReview : {}, {}", id, projectReviewDTO);
        if (projectReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectReviewService
                    .update(projectReviewDTO)
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
     * {@code PATCH  /project-reviews/:id} : Partial updates given fields of an existing projectReview, field will ignore if it is null
     *
     * @param id the id of the projectReviewDTO to save.
     * @param projectReviewDTO the projectReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectReviewDTO,
     * or with status {@code 400 (Bad Request)} if the projectReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-reviews/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectReviewDTO>> partialUpdateProjectReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectReviewDTO projectReviewDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectReview partially : {}, {}", id, projectReviewDTO);
        if (projectReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectReviewDTO> result = projectReviewService.partialUpdate(projectReviewDTO);

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
     * {@code GET  /project-reviews} : get all the projectReviews.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectReviews in body.
     */
    @GetMapping("/project-reviews")
    public Mono<ResponseEntity<List<ProjectReviewDTO>>> getAllProjectReviews(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProjectReviews");
        return projectReviewService
            .countAll()
            .zipWith(projectReviewService.findAll(pageable).collectList())
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
     * {@code GET  /project-reviews/:id} : get the "id" projectReview.
     *
     * @param id the id of the projectReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-reviews/{id}")
    public Mono<ResponseEntity<ProjectReviewDTO>> getProjectReview(@PathVariable Long id) {
        log.debug("REST request to get ProjectReview : {}", id);
        Mono<ProjectReviewDTO> projectReviewDTO = projectReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectReviewDTO);
    }

    /**
     * {@code DELETE  /project-reviews/:id} : delete the "id" projectReview.
     *
     * @param id the id of the projectReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-reviews/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectReview(@PathVariable Long id) {
        log.debug("REST request to delete ProjectReview : {}", id);
        return projectReviewService
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
     * {@code SEARCH  /_search/project-reviews?query=:query} : search for the projectReview corresponding
     * to the query.
     *
     * @param query the query of the projectReview search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-reviews")
    public Mono<ResponseEntity<Flux<ProjectReviewDTO>>> searchProjectReviews(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProjectReviews for query {}", query);
        return projectReviewService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(projectReviewService.search(query, pageable)));
    }
}
