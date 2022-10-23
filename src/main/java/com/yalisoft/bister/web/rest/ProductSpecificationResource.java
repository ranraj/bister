package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductSpecificationRepository;
import com.yalisoft.bister.service.ProductSpecificationService;
import com.yalisoft.bister.service.dto.ProductSpecificationDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductSpecification}.
 */
@RestController
@RequestMapping("/api")
public class ProductSpecificationResource {

    private final Logger log = LoggerFactory.getLogger(ProductSpecificationResource.class);

    private static final String ENTITY_NAME = "productSpecification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductSpecificationService productSpecificationService;

    private final ProductSpecificationRepository productSpecificationRepository;

    public ProductSpecificationResource(
        ProductSpecificationService productSpecificationService,
        ProductSpecificationRepository productSpecificationRepository
    ) {
        this.productSpecificationService = productSpecificationService;
        this.productSpecificationRepository = productSpecificationRepository;
    }

    /**
     * {@code POST  /product-specifications} : Create a new productSpecification.
     *
     * @param productSpecificationDTO the productSpecificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productSpecificationDTO, or with status {@code 400 (Bad Request)} if the productSpecification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-specifications")
    public Mono<ResponseEntity<ProductSpecificationDTO>> createProductSpecification(
        @Valid @RequestBody ProductSpecificationDTO productSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProductSpecification : {}", productSpecificationDTO);
        if (productSpecificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new productSpecification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productSpecificationService
            .save(productSpecificationDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-specifications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-specifications/:id} : Updates an existing productSpecification.
     *
     * @param id the id of the productSpecificationDTO to save.
     * @param productSpecificationDTO the productSpecificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecificationDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productSpecificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-specifications/{id}")
    public Mono<ResponseEntity<ProductSpecificationDTO>> updateProductSpecification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductSpecificationDTO productSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductSpecification : {}, {}", id, productSpecificationDTO);
        if (productSpecificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productSpecificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productSpecificationService
                    .update(productSpecificationDTO)
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
     * {@code PATCH  /product-specifications/:id} : Partial updates given fields of an existing productSpecification, field will ignore if it is null
     *
     * @param id the id of the productSpecificationDTO to save.
     * @param productSpecificationDTO the productSpecificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecificationDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productSpecificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productSpecificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-specifications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductSpecificationDTO>> partialUpdateProductSpecification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductSpecificationDTO productSpecificationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductSpecification partially : {}, {}", id, productSpecificationDTO);
        if (productSpecificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productSpecificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductSpecificationDTO> result = productSpecificationService.partialUpdate(productSpecificationDTO);

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
     * {@code GET  /product-specifications} : get all the productSpecifications.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productSpecifications in body.
     */
    @GetMapping("/product-specifications")
    public Mono<ResponseEntity<List<ProductSpecificationDTO>>> getAllProductSpecifications(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductSpecifications");
        return productSpecificationService
            .countAll()
            .zipWith(productSpecificationService.findAll(pageable).collectList())
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
     * {@code GET  /product-specifications/:id} : get the "id" productSpecification.
     *
     * @param id the id of the productSpecificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productSpecificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-specifications/{id}")
    public Mono<ResponseEntity<ProductSpecificationDTO>> getProductSpecification(@PathVariable Long id) {
        log.debug("REST request to get ProductSpecification : {}", id);
        Mono<ProductSpecificationDTO> productSpecificationDTO = productSpecificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productSpecificationDTO);
    }

    /**
     * {@code DELETE  /product-specifications/:id} : delete the "id" productSpecification.
     *
     * @param id the id of the productSpecificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-specifications/{id}")
    public Mono<ResponseEntity<Void>> deleteProductSpecification(@PathVariable Long id) {
        log.debug("REST request to delete ProductSpecification : {}", id);
        return productSpecificationService
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
     * {@code SEARCH  /_search/product-specifications?query=:query} : search for the productSpecification corresponding
     * to the query.
     *
     * @param query the query of the productSpecification search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-specifications")
    public Mono<ResponseEntity<Flux<ProductSpecificationDTO>>> searchProductSpecifications(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductSpecifications for query {}", query);
        return productSpecificationService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productSpecificationService.search(query, pageable)));
    }
}
