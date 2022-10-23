package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.EnquiryRepository;
import com.yalisoft.bister.service.EnquiryService;
import com.yalisoft.bister.service.dto.EnquiryDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.Enquiry}.
 */
@RestController
@RequestMapping("/api")
public class EnquiryResource {

    private final Logger log = LoggerFactory.getLogger(EnquiryResource.class);

    private static final String ENTITY_NAME = "enquiry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnquiryService enquiryService;

    private final EnquiryRepository enquiryRepository;

    public EnquiryResource(EnquiryService enquiryService, EnquiryRepository enquiryRepository) {
        this.enquiryService = enquiryService;
        this.enquiryRepository = enquiryRepository;
    }

    /**
     * {@code POST  /enquiries} : Create a new enquiry.
     *
     * @param enquiryDTO the enquiryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enquiryDTO, or with status {@code 400 (Bad Request)} if the enquiry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enquiries")
    public Mono<ResponseEntity<EnquiryDTO>> createEnquiry(@Valid @RequestBody EnquiryDTO enquiryDTO) throws URISyntaxException {
        log.debug("REST request to save Enquiry : {}", enquiryDTO);
        if (enquiryDTO.getId() != null) {
            throw new BadRequestAlertException("A new enquiry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return enquiryService
            .save(enquiryDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/enquiries/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /enquiries/:id} : Updates an existing enquiry.
     *
     * @param id the id of the enquiryDTO to save.
     * @param enquiryDTO the enquiryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enquiryDTO,
     * or with status {@code 400 (Bad Request)} if the enquiryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enquiryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enquiries/{id}")
    public Mono<ResponseEntity<EnquiryDTO>> updateEnquiry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnquiryDTO enquiryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Enquiry : {}, {}", id, enquiryDTO);
        if (enquiryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enquiryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enquiryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return enquiryService
                    .update(enquiryDTO)
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
     * {@code PATCH  /enquiries/:id} : Partial updates given fields of an existing enquiry, field will ignore if it is null
     *
     * @param id the id of the enquiryDTO to save.
     * @param enquiryDTO the enquiryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enquiryDTO,
     * or with status {@code 400 (Bad Request)} if the enquiryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the enquiryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the enquiryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/enquiries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EnquiryDTO>> partialUpdateEnquiry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnquiryDTO enquiryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Enquiry partially : {}, {}", id, enquiryDTO);
        if (enquiryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enquiryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enquiryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EnquiryDTO> result = enquiryService.partialUpdate(enquiryDTO);

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
     * {@code GET  /enquiries} : get all the enquiries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enquiries in body.
     */
    @GetMapping("/enquiries")
    public Mono<ResponseEntity<List<EnquiryDTO>>> getAllEnquiries(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Enquiries");
        return enquiryService
            .countAll()
            .zipWith(enquiryService.findAll(pageable).collectList())
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
     * {@code GET  /enquiries/:id} : get the "id" enquiry.
     *
     * @param id the id of the enquiryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enquiryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enquiries/{id}")
    public Mono<ResponseEntity<EnquiryDTO>> getEnquiry(@PathVariable Long id) {
        log.debug("REST request to get Enquiry : {}", id);
        Mono<EnquiryDTO> enquiryDTO = enquiryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(enquiryDTO);
    }

    /**
     * {@code DELETE  /enquiries/:id} : delete the "id" enquiry.
     *
     * @param id the id of the enquiryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enquiries/{id}")
    public Mono<ResponseEntity<Void>> deleteEnquiry(@PathVariable Long id) {
        log.debug("REST request to delete Enquiry : {}", id);
        return enquiryService
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
     * {@code SEARCH  /_search/enquiries?query=:query} : search for the enquiry corresponding
     * to the query.
     *
     * @param query the query of the enquiry search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/enquiries")
    public Mono<ResponseEntity<Flux<EnquiryDTO>>> searchEnquiries(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Enquiries for query {}", query);
        return enquiryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(enquiryService.search(query, pageable)));
    }
}
