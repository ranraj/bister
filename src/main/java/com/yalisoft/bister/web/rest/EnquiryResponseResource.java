package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.EnquiryResponseRepository;
import com.yalisoft.bister.service.EnquiryResponseService;
import com.yalisoft.bister.service.dto.EnquiryResponseDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.EnquiryResponse}.
 */
@RestController
@RequestMapping("/api")
public class EnquiryResponseResource {

    private final Logger log = LoggerFactory.getLogger(EnquiryResponseResource.class);

    private static final String ENTITY_NAME = "enquiryResponse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnquiryResponseService enquiryResponseService;

    private final EnquiryResponseRepository enquiryResponseRepository;

    public EnquiryResponseResource(EnquiryResponseService enquiryResponseService, EnquiryResponseRepository enquiryResponseRepository) {
        this.enquiryResponseService = enquiryResponseService;
        this.enquiryResponseRepository = enquiryResponseRepository;
    }

    /**
     * {@code POST  /enquiry-responses} : Create a new enquiryResponse.
     *
     * @param enquiryResponseDTO the enquiryResponseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enquiryResponseDTO, or with status {@code 400 (Bad Request)} if the enquiryResponse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enquiry-responses")
    public Mono<ResponseEntity<EnquiryResponseDTO>> createEnquiryResponse(@Valid @RequestBody EnquiryResponseDTO enquiryResponseDTO)
        throws URISyntaxException {
        log.debug("REST request to save EnquiryResponse : {}", enquiryResponseDTO);
        if (enquiryResponseDTO.getId() != null) {
            throw new BadRequestAlertException("A new enquiryResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return enquiryResponseService
            .save(enquiryResponseDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/enquiry-responses/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /enquiry-responses/:id} : Updates an existing enquiryResponse.
     *
     * @param id the id of the enquiryResponseDTO to save.
     * @param enquiryResponseDTO the enquiryResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enquiryResponseDTO,
     * or with status {@code 400 (Bad Request)} if the enquiryResponseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enquiryResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enquiry-responses/{id}")
    public Mono<ResponseEntity<EnquiryResponseDTO>> updateEnquiryResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnquiryResponseDTO enquiryResponseDTO
    ) throws URISyntaxException {
        log.debug("REST request to update EnquiryResponse : {}, {}", id, enquiryResponseDTO);
        if (enquiryResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enquiryResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enquiryResponseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return enquiryResponseService
                    .update(enquiryResponseDTO)
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
     * {@code PATCH  /enquiry-responses/:id} : Partial updates given fields of an existing enquiryResponse, field will ignore if it is null
     *
     * @param id the id of the enquiryResponseDTO to save.
     * @param enquiryResponseDTO the enquiryResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enquiryResponseDTO,
     * or with status {@code 400 (Bad Request)} if the enquiryResponseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the enquiryResponseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the enquiryResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/enquiry-responses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EnquiryResponseDTO>> partialUpdateEnquiryResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnquiryResponseDTO enquiryResponseDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update EnquiryResponse partially : {}, {}", id, enquiryResponseDTO);
        if (enquiryResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enquiryResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enquiryResponseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EnquiryResponseDTO> result = enquiryResponseService.partialUpdate(enquiryResponseDTO);

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
     * {@code GET  /enquiry-responses} : get all the enquiryResponses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enquiryResponses in body.
     */
    @GetMapping("/enquiry-responses")
    public Mono<ResponseEntity<List<EnquiryResponseDTO>>> getAllEnquiryResponses(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of EnquiryResponses");
        return enquiryResponseService
            .countAll()
            .zipWith(enquiryResponseService.findAll(pageable).collectList())
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
     * {@code GET  /enquiry-responses/:id} : get the "id" enquiryResponse.
     *
     * @param id the id of the enquiryResponseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enquiryResponseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enquiry-responses/{id}")
    public Mono<ResponseEntity<EnquiryResponseDTO>> getEnquiryResponse(@PathVariable Long id) {
        log.debug("REST request to get EnquiryResponse : {}", id);
        Mono<EnquiryResponseDTO> enquiryResponseDTO = enquiryResponseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(enquiryResponseDTO);
    }

    /**
     * {@code DELETE  /enquiry-responses/:id} : delete the "id" enquiryResponse.
     *
     * @param id the id of the enquiryResponseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enquiry-responses/{id}")
    public Mono<ResponseEntity<Void>> deleteEnquiryResponse(@PathVariable Long id) {
        log.debug("REST request to delete EnquiryResponse : {}", id);
        return enquiryResponseService
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
     * {@code SEARCH  /_search/enquiry-responses?query=:query} : search for the enquiryResponse corresponding
     * to the query.
     *
     * @param query the query of the enquiryResponse search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/enquiry-responses")
    public Mono<ResponseEntity<Flux<EnquiryResponseDTO>>> searchEnquiryResponses(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of EnquiryResponses for query {}", query);
        return enquiryResponseService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(enquiryResponseService.search(query, pageable)));
    }
}
