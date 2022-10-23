package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.PaymentScheduleRepository;
import com.yalisoft.bister.service.PaymentScheduleService;
import com.yalisoft.bister.service.dto.PaymentScheduleDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.PaymentSchedule}.
 */
@RestController
@RequestMapping("/api")
public class PaymentScheduleResource {

    private final Logger log = LoggerFactory.getLogger(PaymentScheduleResource.class);

    private static final String ENTITY_NAME = "paymentSchedule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentScheduleService paymentScheduleService;

    private final PaymentScheduleRepository paymentScheduleRepository;

    public PaymentScheduleResource(PaymentScheduleService paymentScheduleService, PaymentScheduleRepository paymentScheduleRepository) {
        this.paymentScheduleService = paymentScheduleService;
        this.paymentScheduleRepository = paymentScheduleRepository;
    }

    /**
     * {@code POST  /payment-schedules} : Create a new paymentSchedule.
     *
     * @param paymentScheduleDTO the paymentScheduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentScheduleDTO, or with status {@code 400 (Bad Request)} if the paymentSchedule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-schedules")
    public Mono<ResponseEntity<PaymentScheduleDTO>> createPaymentSchedule(@Valid @RequestBody PaymentScheduleDTO paymentScheduleDTO)
        throws URISyntaxException {
        log.debug("REST request to save PaymentSchedule : {}", paymentScheduleDTO);
        if (paymentScheduleDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentSchedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return paymentScheduleService
            .save(paymentScheduleDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/payment-schedules/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /payment-schedules/:id} : Updates an existing paymentSchedule.
     *
     * @param id the id of the paymentScheduleDTO to save.
     * @param paymentScheduleDTO the paymentScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the paymentScheduleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-schedules/{id}")
    public Mono<ResponseEntity<PaymentScheduleDTO>> updatePaymentSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentScheduleDTO paymentScheduleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PaymentSchedule : {}, {}", id, paymentScheduleDTO);
        if (paymentScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentScheduleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return paymentScheduleService
                    .update(paymentScheduleDTO)
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
     * {@code PATCH  /payment-schedules/:id} : Partial updates given fields of an existing paymentSchedule, field will ignore if it is null
     *
     * @param id the id of the paymentScheduleDTO to save.
     * @param paymentScheduleDTO the paymentScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the paymentScheduleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentScheduleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/payment-schedules/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PaymentScheduleDTO>> partialUpdatePaymentSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentScheduleDTO paymentScheduleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PaymentSchedule partially : {}, {}", id, paymentScheduleDTO);
        if (paymentScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentScheduleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PaymentScheduleDTO> result = paymentScheduleService.partialUpdate(paymentScheduleDTO);

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
     * {@code GET  /payment-schedules} : get all the paymentSchedules.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentSchedules in body.
     */
    @GetMapping("/payment-schedules")
    public Mono<ResponseEntity<List<PaymentScheduleDTO>>> getAllPaymentSchedules(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of PaymentSchedules");
        return paymentScheduleService
            .countAll()
            .zipWith(paymentScheduleService.findAll(pageable).collectList())
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
     * {@code GET  /payment-schedules/:id} : get the "id" paymentSchedule.
     *
     * @param id the id of the paymentScheduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentScheduleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-schedules/{id}")
    public Mono<ResponseEntity<PaymentScheduleDTO>> getPaymentSchedule(@PathVariable Long id) {
        log.debug("REST request to get PaymentSchedule : {}", id);
        Mono<PaymentScheduleDTO> paymentScheduleDTO = paymentScheduleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentScheduleDTO);
    }

    /**
     * {@code DELETE  /payment-schedules/:id} : delete the "id" paymentSchedule.
     *
     * @param id the id of the paymentScheduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-schedules/{id}")
    public Mono<ResponseEntity<Void>> deletePaymentSchedule(@PathVariable Long id) {
        log.debug("REST request to delete PaymentSchedule : {}", id);
        return paymentScheduleService
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
     * {@code SEARCH  /_search/payment-schedules?query=:query} : search for the paymentSchedule corresponding
     * to the query.
     *
     * @param query the query of the paymentSchedule search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/payment-schedules")
    public Mono<ResponseEntity<Flux<PaymentScheduleDTO>>> searchPaymentSchedules(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of PaymentSchedules for query {}", query);
        return paymentScheduleService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(paymentScheduleService.search(query, pageable)));
    }
}
