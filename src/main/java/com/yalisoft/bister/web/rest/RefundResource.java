package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.RefundRepository;
import com.yalisoft.bister.service.RefundService;
import com.yalisoft.bister.service.dto.RefundDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.Refund}.
 */
@RestController
@RequestMapping("/api")
public class RefundResource {

    private final Logger log = LoggerFactory.getLogger(RefundResource.class);

    private static final String ENTITY_NAME = "refund";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RefundService refundService;

    private final RefundRepository refundRepository;

    public RefundResource(RefundService refundService, RefundRepository refundRepository) {
        this.refundService = refundService;
        this.refundRepository = refundRepository;
    }

    /**
     * {@code POST  /refunds} : Create a new refund.
     *
     * @param refundDTO the refundDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new refundDTO, or with status {@code 400 (Bad Request)} if the refund has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/refunds")
    public Mono<ResponseEntity<RefundDTO>> createRefund(@Valid @RequestBody RefundDTO refundDTO) throws URISyntaxException {
        log.debug("REST request to save Refund : {}", refundDTO);
        if (refundDTO.getId() != null) {
            throw new BadRequestAlertException("A new refund cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return refundService
            .save(refundDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/refunds/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /refunds/:id} : Updates an existing refund.
     *
     * @param id the id of the refundDTO to save.
     * @param refundDTO the refundDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundDTO,
     * or with status {@code 400 (Bad Request)} if the refundDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the refundDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/refunds/{id}")
    public Mono<ResponseEntity<RefundDTO>> updateRefund(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RefundDTO refundDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Refund : {}, {}", id, refundDTO);
        if (refundDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return refundRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return refundService
                    .update(refundDTO)
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
     * {@code PATCH  /refunds/:id} : Partial updates given fields of an existing refund, field will ignore if it is null
     *
     * @param id the id of the refundDTO to save.
     * @param refundDTO the refundDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundDTO,
     * or with status {@code 400 (Bad Request)} if the refundDTO is not valid,
     * or with status {@code 404 (Not Found)} if the refundDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the refundDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/refunds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RefundDTO>> partialUpdateRefund(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RefundDTO refundDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Refund partially : {}, {}", id, refundDTO);
        if (refundDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return refundRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RefundDTO> result = refundService.partialUpdate(refundDTO);

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
     * {@code GET  /refunds} : get all the refunds.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of refunds in body.
     */
    @GetMapping("/refunds")
    public Mono<ResponseEntity<List<RefundDTO>>> getAllRefunds(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Refunds");
        return refundService
            .countAll()
            .zipWith(refundService.findAll(pageable).collectList())
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
     * {@code GET  /refunds/:id} : get the "id" refund.
     *
     * @param id the id of the refundDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the refundDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/refunds/{id}")
    public Mono<ResponseEntity<RefundDTO>> getRefund(@PathVariable Long id) {
        log.debug("REST request to get Refund : {}", id);
        Mono<RefundDTO> refundDTO = refundService.findOne(id);
        return ResponseUtil.wrapOrNotFound(refundDTO);
    }

    /**
     * {@code DELETE  /refunds/:id} : delete the "id" refund.
     *
     * @param id the id of the refundDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/refunds/{id}")
    public Mono<ResponseEntity<Void>> deleteRefund(@PathVariable Long id) {
        log.debug("REST request to delete Refund : {}", id);
        return refundService
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
     * {@code SEARCH  /_search/refunds?query=:query} : search for the refund corresponding
     * to the query.
     *
     * @param query the query of the refund search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/refunds")
    public Mono<ResponseEntity<Flux<RefundDTO>>> searchRefunds(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Refunds for query {}", query);
        return refundService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(refundService.search(query, pageable)));
    }
}
