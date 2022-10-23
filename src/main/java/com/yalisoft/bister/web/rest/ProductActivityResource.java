package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductActivityRepository;
import com.yalisoft.bister.service.ProductActivityService;
import com.yalisoft.bister.service.dto.ProductActivityDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductActivity}.
 */
@RestController
@RequestMapping("/api")
public class ProductActivityResource {

    private final Logger log = LoggerFactory.getLogger(ProductActivityResource.class);

    private static final String ENTITY_NAME = "productActivity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductActivityService productActivityService;

    private final ProductActivityRepository productActivityRepository;

    public ProductActivityResource(ProductActivityService productActivityService, ProductActivityRepository productActivityRepository) {
        this.productActivityService = productActivityService;
        this.productActivityRepository = productActivityRepository;
    }

    /**
     * {@code POST  /product-activities} : Create a new productActivity.
     *
     * @param productActivityDTO the productActivityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productActivityDTO, or with status {@code 400 (Bad Request)} if the productActivity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-activities")
    public Mono<ResponseEntity<ProductActivityDTO>> createProductActivity(@Valid @RequestBody ProductActivityDTO productActivityDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductActivity : {}", productActivityDTO);
        if (productActivityDTO.getId() != null) {
            throw new BadRequestAlertException("A new productActivity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productActivityService
            .save(productActivityDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-activities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-activities/:id} : Updates an existing productActivity.
     *
     * @param id the id of the productActivityDTO to save.
     * @param productActivityDTO the productActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productActivityDTO,
     * or with status {@code 400 (Bad Request)} if the productActivityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-activities/{id}")
    public Mono<ResponseEntity<ProductActivityDTO>> updateProductActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductActivityDTO productActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductActivity : {}, {}", id, productActivityDTO);
        if (productActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productActivityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productActivityService
                    .update(productActivityDTO)
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
     * {@code PATCH  /product-activities/:id} : Partial updates given fields of an existing productActivity, field will ignore if it is null
     *
     * @param id the id of the productActivityDTO to save.
     * @param productActivityDTO the productActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productActivityDTO,
     * or with status {@code 400 (Bad Request)} if the productActivityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productActivityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-activities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductActivityDTO>> partialUpdateProductActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductActivityDTO productActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductActivity partially : {}, {}", id, productActivityDTO);
        if (productActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productActivityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductActivityDTO> result = productActivityService.partialUpdate(productActivityDTO);

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
     * {@code GET  /product-activities} : get all the productActivities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productActivities in body.
     */
    @GetMapping("/product-activities")
    public Mono<ResponseEntity<List<ProductActivityDTO>>> getAllProductActivities(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductActivities");
        return productActivityService
            .countAll()
            .zipWith(productActivityService.findAll(pageable).collectList())
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
     * {@code GET  /product-activities/:id} : get the "id" productActivity.
     *
     * @param id the id of the productActivityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productActivityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-activities/{id}")
    public Mono<ResponseEntity<ProductActivityDTO>> getProductActivity(@PathVariable Long id) {
        log.debug("REST request to get ProductActivity : {}", id);
        Mono<ProductActivityDTO> productActivityDTO = productActivityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productActivityDTO);
    }

    /**
     * {@code DELETE  /product-activities/:id} : delete the "id" productActivity.
     *
     * @param id the id of the productActivityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-activities/{id}")
    public Mono<ResponseEntity<Void>> deleteProductActivity(@PathVariable Long id) {
        log.debug("REST request to delete ProductActivity : {}", id);
        return productActivityService
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
     * {@code SEARCH  /_search/product-activities?query=:query} : search for the productActivity corresponding
     * to the query.
     *
     * @param query the query of the productActivity search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-activities")
    public Mono<ResponseEntity<Flux<ProductActivityDTO>>> searchProductActivities(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductActivities for query {}", query);
        return productActivityService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productActivityService.search(query, pageable)));
    }
}
