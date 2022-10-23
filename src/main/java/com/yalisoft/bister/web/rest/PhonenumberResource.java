package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.PhonenumberRepository;
import com.yalisoft.bister.service.PhonenumberService;
import com.yalisoft.bister.service.dto.PhonenumberDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.Phonenumber}.
 */
@RestController
@RequestMapping("/api")
public class PhonenumberResource {

    private final Logger log = LoggerFactory.getLogger(PhonenumberResource.class);

    private static final String ENTITY_NAME = "phonenumber";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PhonenumberService phonenumberService;

    private final PhonenumberRepository phonenumberRepository;

    public PhonenumberResource(PhonenumberService phonenumberService, PhonenumberRepository phonenumberRepository) {
        this.phonenumberService = phonenumberService;
        this.phonenumberRepository = phonenumberRepository;
    }

    /**
     * {@code POST  /phonenumbers} : Create a new phonenumber.
     *
     * @param phonenumberDTO the phonenumberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new phonenumberDTO, or with status {@code 400 (Bad Request)} if the phonenumber has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/phonenumbers")
    public Mono<ResponseEntity<PhonenumberDTO>> createPhonenumber(@Valid @RequestBody PhonenumberDTO phonenumberDTO)
        throws URISyntaxException {
        log.debug("REST request to save Phonenumber : {}", phonenumberDTO);
        if (phonenumberDTO.getId() != null) {
            throw new BadRequestAlertException("A new phonenumber cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return phonenumberService
            .save(phonenumberDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/phonenumbers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /phonenumbers/:id} : Updates an existing phonenumber.
     *
     * @param id the id of the phonenumberDTO to save.
     * @param phonenumberDTO the phonenumberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated phonenumberDTO,
     * or with status {@code 400 (Bad Request)} if the phonenumberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the phonenumberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/phonenumbers/{id}")
    public Mono<ResponseEntity<PhonenumberDTO>> updatePhonenumber(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PhonenumberDTO phonenumberDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Phonenumber : {}, {}", id, phonenumberDTO);
        if (phonenumberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, phonenumberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return phonenumberRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return phonenumberService
                    .update(phonenumberDTO)
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
     * {@code PATCH  /phonenumbers/:id} : Partial updates given fields of an existing phonenumber, field will ignore if it is null
     *
     * @param id the id of the phonenumberDTO to save.
     * @param phonenumberDTO the phonenumberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated phonenumberDTO,
     * or with status {@code 400 (Bad Request)} if the phonenumberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the phonenumberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the phonenumberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/phonenumbers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PhonenumberDTO>> partialUpdatePhonenumber(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PhonenumberDTO phonenumberDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Phonenumber partially : {}, {}", id, phonenumberDTO);
        if (phonenumberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, phonenumberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return phonenumberRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PhonenumberDTO> result = phonenumberService.partialUpdate(phonenumberDTO);

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
     * {@code GET  /phonenumbers} : get all the phonenumbers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of phonenumbers in body.
     */
    @GetMapping("/phonenumbers")
    public Mono<ResponseEntity<List<PhonenumberDTO>>> getAllPhonenumbers(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Phonenumbers");
        return phonenumberService
            .countAll()
            .zipWith(phonenumberService.findAll(pageable).collectList())
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
     * {@code GET  /phonenumbers/:id} : get the "id" phonenumber.
     *
     * @param id the id of the phonenumberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the phonenumberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/phonenumbers/{id}")
    public Mono<ResponseEntity<PhonenumberDTO>> getPhonenumber(@PathVariable Long id) {
        log.debug("REST request to get Phonenumber : {}", id);
        Mono<PhonenumberDTO> phonenumberDTO = phonenumberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(phonenumberDTO);
    }

    /**
     * {@code DELETE  /phonenumbers/:id} : delete the "id" phonenumber.
     *
     * @param id the id of the phonenumberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/phonenumbers/{id}")
    public Mono<ResponseEntity<Void>> deletePhonenumber(@PathVariable Long id) {
        log.debug("REST request to delete Phonenumber : {}", id);
        return phonenumberService
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
     * {@code SEARCH  /_search/phonenumbers?query=:query} : search for the phonenumber corresponding
     * to the query.
     *
     * @param query the query of the phonenumber search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/phonenumbers")
    public Mono<ResponseEntity<Flux<PhonenumberDTO>>> searchPhonenumbers(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Phonenumbers for query {}", query);
        return phonenumberService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(phonenumberService.search(query, pageable)));
    }
}
