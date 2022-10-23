package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.BookingOrderRepository;
import com.yalisoft.bister.service.BookingOrderService;
import com.yalisoft.bister.service.dto.BookingOrderDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.BookingOrder}.
 */
@RestController
@RequestMapping("/api")
public class BookingOrderResource {

    private final Logger log = LoggerFactory.getLogger(BookingOrderResource.class);

    private static final String ENTITY_NAME = "bookingOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookingOrderService bookingOrderService;

    private final BookingOrderRepository bookingOrderRepository;

    public BookingOrderResource(BookingOrderService bookingOrderService, BookingOrderRepository bookingOrderRepository) {
        this.bookingOrderService = bookingOrderService;
        this.bookingOrderRepository = bookingOrderRepository;
    }

    /**
     * {@code POST  /booking-orders} : Create a new bookingOrder.
     *
     * @param bookingOrderDTO the bookingOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookingOrderDTO, or with status {@code 400 (Bad Request)} if the bookingOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/booking-orders")
    public Mono<ResponseEntity<BookingOrderDTO>> createBookingOrder(@Valid @RequestBody BookingOrderDTO bookingOrderDTO)
        throws URISyntaxException {
        log.debug("REST request to save BookingOrder : {}", bookingOrderDTO);
        if (bookingOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookingOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bookingOrderService
            .save(bookingOrderDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/booking-orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /booking-orders/:id} : Updates an existing bookingOrder.
     *
     * @param id the id of the bookingOrderDTO to save.
     * @param bookingOrderDTO the bookingOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingOrderDTO,
     * or with status {@code 400 (Bad Request)} if the bookingOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookingOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/booking-orders/{id}")
    public Mono<ResponseEntity<BookingOrderDTO>> updateBookingOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookingOrderDTO bookingOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BookingOrder : {}, {}", id, bookingOrderDTO);
        if (bookingOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bookingOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return bookingOrderService
                    .update(bookingOrderDTO)
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
     * {@code PATCH  /booking-orders/:id} : Partial updates given fields of an existing bookingOrder, field will ignore if it is null
     *
     * @param id the id of the bookingOrderDTO to save.
     * @param bookingOrderDTO the bookingOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingOrderDTO,
     * or with status {@code 400 (Bad Request)} if the bookingOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookingOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookingOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/booking-orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BookingOrderDTO>> partialUpdateBookingOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookingOrderDTO bookingOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BookingOrder partially : {}, {}", id, bookingOrderDTO);
        if (bookingOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bookingOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BookingOrderDTO> result = bookingOrderService.partialUpdate(bookingOrderDTO);

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
     * {@code GET  /booking-orders} : get all the bookingOrders.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookingOrders in body.
     */
    @GetMapping("/booking-orders")
    public Mono<ResponseEntity<List<BookingOrderDTO>>> getAllBookingOrders(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of BookingOrders");
        return bookingOrderService
            .countAll()
            .zipWith(bookingOrderService.findAll(pageable).collectList())
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
     * {@code GET  /booking-orders/:id} : get the "id" bookingOrder.
     *
     * @param id the id of the bookingOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookingOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/booking-orders/{id}")
    public Mono<ResponseEntity<BookingOrderDTO>> getBookingOrder(@PathVariable Long id) {
        log.debug("REST request to get BookingOrder : {}", id);
        Mono<BookingOrderDTO> bookingOrderDTO = bookingOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookingOrderDTO);
    }

    /**
     * {@code DELETE  /booking-orders/:id} : delete the "id" bookingOrder.
     *
     * @param id the id of the bookingOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/booking-orders/{id}")
    public Mono<ResponseEntity<Void>> deleteBookingOrder(@PathVariable Long id) {
        log.debug("REST request to delete BookingOrder : {}", id);
        return bookingOrderService
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
     * {@code SEARCH  /_search/booking-orders?query=:query} : search for the bookingOrder corresponding
     * to the query.
     *
     * @param query the query of the bookingOrder search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/booking-orders")
    public Mono<ResponseEntity<Flux<BookingOrderDTO>>> searchBookingOrders(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of BookingOrders for query {}", query);
        return bookingOrderService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(bookingOrderService.search(query, pageable)));
    }
}
