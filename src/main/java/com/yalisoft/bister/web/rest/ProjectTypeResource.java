package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProjectTypeRepository;
import com.yalisoft.bister.service.ProjectTypeService;
import com.yalisoft.bister.service.dto.ProjectTypeDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProjectType}.
 */
@RestController
@RequestMapping("/api")
public class ProjectTypeResource {

    private final Logger log = LoggerFactory.getLogger(ProjectTypeResource.class);

    private static final String ENTITY_NAME = "projectType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectTypeService projectTypeService;

    private final ProjectTypeRepository projectTypeRepository;

    public ProjectTypeResource(ProjectTypeService projectTypeService, ProjectTypeRepository projectTypeRepository) {
        this.projectTypeService = projectTypeService;
        this.projectTypeRepository = projectTypeRepository;
    }

    /**
     * {@code POST  /project-types} : Create a new projectType.
     *
     * @param projectTypeDTO the projectTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectTypeDTO, or with status {@code 400 (Bad Request)} if the projectType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-types")
    public Mono<ResponseEntity<ProjectTypeDTO>> createProjectType(@Valid @RequestBody ProjectTypeDTO projectTypeDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectType : {}", projectTypeDTO);
        if (projectTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectTypeService
            .save(projectTypeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/project-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /project-types/:id} : Updates an existing projectType.
     *
     * @param id the id of the projectTypeDTO to save.
     * @param projectTypeDTO the projectTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectTypeDTO,
     * or with status {@code 400 (Bad Request)} if the projectTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-types/{id}")
    public Mono<ResponseEntity<ProjectTypeDTO>> updateProjectType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectTypeDTO projectTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectType : {}, {}", id, projectTypeDTO);
        if (projectTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectTypeService
                    .update(projectTypeDTO)
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
     * {@code PATCH  /project-types/:id} : Partial updates given fields of an existing projectType, field will ignore if it is null
     *
     * @param id the id of the projectTypeDTO to save.
     * @param projectTypeDTO the projectTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectTypeDTO,
     * or with status {@code 400 (Bad Request)} if the projectTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectTypeDTO>> partialUpdateProjectType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectTypeDTO projectTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectType partially : {}, {}", id, projectTypeDTO);
        if (projectTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectTypeDTO> result = projectTypeService.partialUpdate(projectTypeDTO);

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
     * {@code GET  /project-types} : get all the projectTypes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectTypes in body.
     */
    @GetMapping("/project-types")
    public Mono<ResponseEntity<List<ProjectTypeDTO>>> getAllProjectTypes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of ProjectTypes");
        return projectTypeService
            .countAll()
            .zipWith(projectTypeService.findAll(pageable).collectList())
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
     * {@code GET  /project-types/:id} : get the "id" projectType.
     *
     * @param id the id of the projectTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-types/{id}")
    public Mono<ResponseEntity<ProjectTypeDTO>> getProjectType(@PathVariable Long id) {
        log.debug("REST request to get ProjectType : {}", id);
        Mono<ProjectTypeDTO> projectTypeDTO = projectTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectTypeDTO);
    }

    /**
     * {@code DELETE  /project-types/:id} : delete the "id" projectType.
     *
     * @param id the id of the projectTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-types/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectType(@PathVariable Long id) {
        log.debug("REST request to delete ProjectType : {}", id);
        return projectTypeService
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
     * {@code SEARCH  /_search/project-types?query=:query} : search for the projectType corresponding
     * to the query.
     *
     * @param query the query of the projectType search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-types")
    public Mono<ResponseEntity<Flux<ProjectTypeDTO>>> searchProjectTypes(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProjectTypes for query {}", query);
        return projectTypeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(projectTypeService.search(query, pageable)));
    }
}
