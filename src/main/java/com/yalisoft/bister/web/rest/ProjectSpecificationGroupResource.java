package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProjectSpecificationGroupRepository;
import com.yalisoft.bister.service.ProjectSpecificationGroupService;
import com.yalisoft.bister.service.dto.ProjectSpecificationGroupDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProjectSpecificationGroup}.
 */
@RestController
@RequestMapping("/api")
public class ProjectSpecificationGroupResource {

    private final Logger log = LoggerFactory.getLogger(ProjectSpecificationGroupResource.class);

    private static final String ENTITY_NAME = "projectSpecificationGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectSpecificationGroupService projectSpecificationGroupService;

    private final ProjectSpecificationGroupRepository projectSpecificationGroupRepository;

    public ProjectSpecificationGroupResource(
        ProjectSpecificationGroupService projectSpecificationGroupService,
        ProjectSpecificationGroupRepository projectSpecificationGroupRepository
    ) {
        this.projectSpecificationGroupService = projectSpecificationGroupService;
        this.projectSpecificationGroupRepository = projectSpecificationGroupRepository;
    }

    /**
     * {@code POST  /project-specification-groups} : Create a new projectSpecificationGroup.
     *
     * @param projectSpecificationGroupDTO the projectSpecificationGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectSpecificationGroupDTO, or with status {@code 400 (Bad Request)} if the projectSpecificationGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-specification-groups")
    public Mono<ResponseEntity<ProjectSpecificationGroupDTO>> createProjectSpecificationGroup(
        @Valid @RequestBody ProjectSpecificationGroupDTO projectSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProjectSpecificationGroup : {}", projectSpecificationGroupDTO);
        if (projectSpecificationGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectSpecificationGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectSpecificationGroupService
            .save(projectSpecificationGroupDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/project-specification-groups/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /project-specification-groups/:id} : Updates an existing projectSpecificationGroup.
     *
     * @param id the id of the projectSpecificationGroupDTO to save.
     * @param projectSpecificationGroupDTO the projectSpecificationGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSpecificationGroupDTO,
     * or with status {@code 400 (Bad Request)} if the projectSpecificationGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectSpecificationGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-specification-groups/{id}")
    public Mono<ResponseEntity<ProjectSpecificationGroupDTO>> updateProjectSpecificationGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectSpecificationGroupDTO projectSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectSpecificationGroup : {}, {}", id, projectSpecificationGroupDTO);
        if (projectSpecificationGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSpecificationGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSpecificationGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectSpecificationGroupService
                    .update(projectSpecificationGroupDTO)
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
     * {@code PATCH  /project-specification-groups/:id} : Partial updates given fields of an existing projectSpecificationGroup, field will ignore if it is null
     *
     * @param id the id of the projectSpecificationGroupDTO to save.
     * @param projectSpecificationGroupDTO the projectSpecificationGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSpecificationGroupDTO,
     * or with status {@code 400 (Bad Request)} if the projectSpecificationGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectSpecificationGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectSpecificationGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-specification-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectSpecificationGroupDTO>> partialUpdateProjectSpecificationGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectSpecificationGroupDTO projectSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectSpecificationGroup partially : {}, {}", id, projectSpecificationGroupDTO);
        if (projectSpecificationGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSpecificationGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSpecificationGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectSpecificationGroupDTO> result = projectSpecificationGroupService.partialUpdate(projectSpecificationGroupDTO);

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
     * {@code GET  /project-specification-groups} : get all the projectSpecificationGroups.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectSpecificationGroups in body.
     */
    @GetMapping("/project-specification-groups")
    public Mono<ResponseEntity<List<ProjectSpecificationGroupDTO>>> getAllProjectSpecificationGroups(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProjectSpecificationGroups");
        return projectSpecificationGroupService
            .countAll()
            .zipWith(projectSpecificationGroupService.findAll(pageable).collectList())
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
     * {@code GET  /project-specification-groups/:id} : get the "id" projectSpecificationGroup.
     *
     * @param id the id of the projectSpecificationGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectSpecificationGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-specification-groups/{id}")
    public Mono<ResponseEntity<ProjectSpecificationGroupDTO>> getProjectSpecificationGroup(@PathVariable Long id) {
        log.debug("REST request to get ProjectSpecificationGroup : {}", id);
        Mono<ProjectSpecificationGroupDTO> projectSpecificationGroupDTO = projectSpecificationGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectSpecificationGroupDTO);
    }

    /**
     * {@code DELETE  /project-specification-groups/:id} : delete the "id" projectSpecificationGroup.
     *
     * @param id the id of the projectSpecificationGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-specification-groups/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectSpecificationGroup(@PathVariable Long id) {
        log.debug("REST request to delete ProjectSpecificationGroup : {}", id);
        return projectSpecificationGroupService
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
     * {@code SEARCH  /_search/project-specification-groups?query=:query} : search for the projectSpecificationGroup corresponding
     * to the query.
     *
     * @param query the query of the projectSpecificationGroup search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-specification-groups")
    public Mono<ResponseEntity<Flux<ProjectSpecificationGroupDTO>>> searchProjectSpecificationGroups(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProjectSpecificationGroups for query {}", query);
        return projectSpecificationGroupService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(projectSpecificationGroupService.search(query, pageable)));
    }
}
