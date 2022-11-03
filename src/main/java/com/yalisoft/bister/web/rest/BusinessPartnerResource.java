package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.BusinessPartnerRepository;
import com.yalisoft.bister.security.AuthoritiesConstants;
import com.yalisoft.bister.service.BusinessPartnerService;
import com.yalisoft.bister.service.dto.BusinessPartnerDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.yalisoft.bister.domain.BusinessPartner}.
 */
@RestController
@RequestMapping("/api")
public class BusinessPartnerResource {

    private final Logger log = LoggerFactory.getLogger(BusinessPartnerResource.class);

    private static final String ENTITY_NAME = "businessPartner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessPartnerService businessPartnerService;

    private final BusinessPartnerRepository businessPartnerRepository;

    public BusinessPartnerResource(BusinessPartnerService businessPartnerService, BusinessPartnerRepository businessPartnerRepository) {
        this.businessPartnerService = businessPartnerService;
        this.businessPartnerRepository = businessPartnerRepository;
    }

    /**
     * {@code POST  /business-partners} : Create a new businessPartner.
     *
     * @param businessPartnerDTO the businessPartnerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessPartnerDTO, or with status {@code 400 (Bad Request)} if the businessPartner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/business-partners")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<BusinessPartnerDTO>> createBusinessPartner(@Valid @RequestBody BusinessPartnerDTO businessPartnerDTO)
        throws URISyntaxException {
        log.debug("REST request to save BusinessPartner : {}", businessPartnerDTO);
        if (businessPartnerDTO.getId() != null) {
            throw new BadRequestAlertException("A new businessPartner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return businessPartnerService
            .save(businessPartnerDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/business-partners/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /business-partners/:id} : Updates an existing businessPartner.
     *
     * @param id the id of the businessPartnerDTO to save.
     * @param businessPartnerDTO the businessPartnerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessPartnerDTO,
     * or with status {@code 400 (Bad Request)} if the businessPartnerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessPartnerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/business-partners/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<BusinessPartnerDTO>> updateBusinessPartner(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BusinessPartnerDTO businessPartnerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BusinessPartner : {}, {}", id, businessPartnerDTO);
        if (businessPartnerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessPartnerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return businessPartnerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return businessPartnerService
                    .update(businessPartnerDTO)
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
     * {@code PATCH  /business-partners/:id} : Partial updates given fields of an existing businessPartner, field will ignore if it is null
     *
     * @param id the id of the businessPartnerDTO to save.
     * @param businessPartnerDTO the businessPartnerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessPartnerDTO,
     * or with status {@code 400 (Bad Request)} if the businessPartnerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessPartnerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessPartnerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/business-partners/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<BusinessPartnerDTO>> partialUpdateBusinessPartner(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BusinessPartnerDTO businessPartnerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BusinessPartner partially : {}, {}", id, businessPartnerDTO);
        if (businessPartnerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessPartnerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return businessPartnerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BusinessPartnerDTO> result = businessPartnerService.partialUpdate(businessPartnerDTO);

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
     * {@code GET  /business-partners} : get all the businessPartners.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessPartners in body.
     */
    @GetMapping("/business-partners")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<List<BusinessPartnerDTO>>> getAllBusinessPartners(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of BusinessPartners");
        return businessPartnerService
            .countAll()
            .zipWith(businessPartnerService.findAll(pageable).collectList())
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
     * {@code GET  /business-partners/:id} : get the "id" businessPartner.
     *
     * @param id the id of the businessPartnerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessPartnerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/business-partners/{id}")
    public Mono<ResponseEntity<BusinessPartnerDTO>> getBusinessPartner(@PathVariable Long id) {
        log.debug("REST request to get BusinessPartner : {}", id);
        Mono<BusinessPartnerDTO> businessPartnerDTO = businessPartnerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessPartnerDTO);
    }

    /**
     * {@code DELETE  /business-partners/:id} : delete the "id" businessPartner.
     *
     * @param id the id of the businessPartnerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/business-partners/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Void>> deleteBusinessPartner(@PathVariable Long id) {
        log.debug("REST request to delete BusinessPartner : {}", id);
        return businessPartnerService
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
     * {@code SEARCH  /_search/business-partners?query=:query} : search for the businessPartner corresponding
     * to the query.
     *
     * @param query the query of the businessPartner search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/business-partners")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Flux<BusinessPartnerDTO>>> searchBusinessPartners(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of BusinessPartners for query {}", query);
        return businessPartnerService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(businessPartnerService.search(query, pageable)));
    }
}
