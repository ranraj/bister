package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.TaxClassRepository;
import com.yalisoft.bister.service.TaxClassService;
import com.yalisoft.bister.service.dto.TaxClassDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.TaxClass}.
 */
@RestController
@RequestMapping("/api")
public class TaxClassResource {

    private final Logger log = LoggerFactory.getLogger(TaxClassResource.class);

    private static final String ENTITY_NAME = "taxClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxClassService taxClassService;

    private final TaxClassRepository taxClassRepository;

    public TaxClassResource(TaxClassService taxClassService, TaxClassRepository taxClassRepository) {
        this.taxClassService = taxClassService;
        this.taxClassRepository = taxClassRepository;
    }

    /**
     * {@code POST  /tax-classes} : Create a new taxClass.
     *
     * @param taxClassDTO the taxClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taxClassDTO, or with status {@code 400 (Bad Request)} if the taxClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tax-classes")
    public Mono<ResponseEntity<TaxClassDTO>> createTaxClass(@Valid @RequestBody TaxClassDTO taxClassDTO) throws URISyntaxException {
        log.debug("REST request to save TaxClass : {}", taxClassDTO);
        if (taxClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new taxClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return taxClassService
            .save(taxClassDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tax-classes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tax-classes/:id} : Updates an existing taxClass.
     *
     * @param id the id of the taxClassDTO to save.
     * @param taxClassDTO the taxClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxClassDTO,
     * or with status {@code 400 (Bad Request)} if the taxClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taxClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tax-classes/{id}")
    public Mono<ResponseEntity<TaxClassDTO>> updateTaxClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaxClassDTO taxClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TaxClass : {}, {}", id, taxClassDTO);
        if (taxClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxClassRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return taxClassService
                    .update(taxClassDTO)
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
     * {@code PATCH  /tax-classes/:id} : Partial updates given fields of an existing taxClass, field will ignore if it is null
     *
     * @param id the id of the taxClassDTO to save.
     * @param taxClassDTO the taxClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxClassDTO,
     * or with status {@code 400 (Bad Request)} if the taxClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taxClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taxClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tax-classes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TaxClassDTO>> partialUpdateTaxClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaxClassDTO taxClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaxClass partially : {}, {}", id, taxClassDTO);
        if (taxClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxClassRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TaxClassDTO> result = taxClassService.partialUpdate(taxClassDTO);

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
     * {@code GET  /tax-classes} : get all the taxClasses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taxClasses in body.
     */
    @GetMapping("/tax-classes")
    public Mono<ResponseEntity<List<TaxClassDTO>>> getAllTaxClasses(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of TaxClasses");
        return taxClassService
            .countAll()
            .zipWith(taxClassService.findAll(pageable).collectList())
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
     * {@code GET  /tax-classes/:id} : get the "id" taxClass.
     *
     * @param id the id of the taxClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taxClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tax-classes/{id}")
    public Mono<ResponseEntity<TaxClassDTO>> getTaxClass(@PathVariable Long id) {
        log.debug("REST request to get TaxClass : {}", id);
        Mono<TaxClassDTO> taxClassDTO = taxClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taxClassDTO);
    }

    /**
     * {@code DELETE  /tax-classes/:id} : delete the "id" taxClass.
     *
     * @param id the id of the taxClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tax-classes/{id}")
    public Mono<ResponseEntity<Void>> deleteTaxClass(@PathVariable Long id) {
        log.debug("REST request to delete TaxClass : {}", id);
        return taxClassService
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
     * {@code SEARCH  /_search/tax-classes?query=:query} : search for the taxClass corresponding
     * to the query.
     *
     * @param query the query of the taxClass search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/tax-classes")
    public Mono<ResponseEntity<Flux<TaxClassDTO>>> searchTaxClasses(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TaxClasses for query {}", query);
        return taxClassService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(taxClassService.search(query, pageable)));
    }
}
