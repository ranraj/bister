package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.TaxRateRepository;
import com.yalisoft.bister.service.TaxRateService;
import com.yalisoft.bister.service.dto.TaxRateDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.TaxRate}.
 */
@RestController
@RequestMapping("/api")
public class TaxRateResource {

    private final Logger log = LoggerFactory.getLogger(TaxRateResource.class);

    private static final String ENTITY_NAME = "taxRate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxRateService taxRateService;

    private final TaxRateRepository taxRateRepository;

    public TaxRateResource(TaxRateService taxRateService, TaxRateRepository taxRateRepository) {
        this.taxRateService = taxRateService;
        this.taxRateRepository = taxRateRepository;
    }

    /**
     * {@code POST  /tax-rates} : Create a new taxRate.
     *
     * @param taxRateDTO the taxRateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taxRateDTO, or with status {@code 400 (Bad Request)} if the taxRate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tax-rates")
    public Mono<ResponseEntity<TaxRateDTO>> createTaxRate(@Valid @RequestBody TaxRateDTO taxRateDTO) throws URISyntaxException {
        log.debug("REST request to save TaxRate : {}", taxRateDTO);
        if (taxRateDTO.getId() != null) {
            throw new BadRequestAlertException("A new taxRate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return taxRateService
            .save(taxRateDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tax-rates/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tax-rates/:id} : Updates an existing taxRate.
     *
     * @param id the id of the taxRateDTO to save.
     * @param taxRateDTO the taxRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxRateDTO,
     * or with status {@code 400 (Bad Request)} if the taxRateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taxRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tax-rates/{id}")
    public Mono<ResponseEntity<TaxRateDTO>> updateTaxRate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaxRateDTO taxRateDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TaxRate : {}, {}", id, taxRateDTO);
        if (taxRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxRateRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return taxRateService
                    .update(taxRateDTO)
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
     * {@code PATCH  /tax-rates/:id} : Partial updates given fields of an existing taxRate, field will ignore if it is null
     *
     * @param id the id of the taxRateDTO to save.
     * @param taxRateDTO the taxRateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxRateDTO,
     * or with status {@code 400 (Bad Request)} if the taxRateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taxRateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taxRateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tax-rates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TaxRateDTO>> partialUpdateTaxRate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaxRateDTO taxRateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaxRate partially : {}, {}", id, taxRateDTO);
        if (taxRateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxRateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxRateRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TaxRateDTO> result = taxRateService.partialUpdate(taxRateDTO);

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
     * {@code GET  /tax-rates} : get all the taxRates.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taxRates in body.
     */
    @GetMapping("/tax-rates")
    public Mono<ResponseEntity<List<TaxRateDTO>>> getAllTaxRates(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of TaxRates");
        return taxRateService
            .countAll()
            .zipWith(taxRateService.findAll(pageable).collectList())
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
     * {@code GET  /tax-rates/:id} : get the "id" taxRate.
     *
     * @param id the id of the taxRateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taxRateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tax-rates/{id}")
    public Mono<ResponseEntity<TaxRateDTO>> getTaxRate(@PathVariable Long id) {
        log.debug("REST request to get TaxRate : {}", id);
        Mono<TaxRateDTO> taxRateDTO = taxRateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taxRateDTO);
    }

    /**
     * {@code DELETE  /tax-rates/:id} : delete the "id" taxRate.
     *
     * @param id the id of the taxRateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tax-rates/{id}")
    public Mono<ResponseEntity<Void>> deleteTaxRate(@PathVariable Long id) {
        log.debug("REST request to delete TaxRate : {}", id);
        return taxRateService
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
     * {@code SEARCH  /_search/tax-rates?query=:query} : search for the taxRate corresponding
     * to the query.
     *
     * @param query the query of the taxRate search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/tax-rates")
    public Mono<ResponseEntity<Flux<TaxRateDTO>>> searchTaxRates(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TaxRates for query {}", query);
        return taxRateService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(taxRateService.search(query, pageable)));
    }
}
