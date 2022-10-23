package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductVariationRepository;
import com.yalisoft.bister.service.ProductVariationService;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductVariation}.
 */
@RestController
@RequestMapping("/api")
public class ProductVariationResource {

    private final Logger log = LoggerFactory.getLogger(ProductVariationResource.class);

    private static final String ENTITY_NAME = "productVariation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductVariationService productVariationService;

    private final ProductVariationRepository productVariationRepository;

    public ProductVariationResource(
        ProductVariationService productVariationService,
        ProductVariationRepository productVariationRepository
    ) {
        this.productVariationService = productVariationService;
        this.productVariationRepository = productVariationRepository;
    }

    /**
     * {@code POST  /product-variations} : Create a new productVariation.
     *
     * @param productVariationDTO the productVariationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productVariationDTO, or with status {@code 400 (Bad Request)} if the productVariation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-variations")
    public Mono<ResponseEntity<ProductVariationDTO>> createProductVariation(@Valid @RequestBody ProductVariationDTO productVariationDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductVariation : {}", productVariationDTO);
        if (productVariationDTO.getId() != null) {
            throw new BadRequestAlertException("A new productVariation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productVariationService
            .save(productVariationDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-variations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-variations/:id} : Updates an existing productVariation.
     *
     * @param id the id of the productVariationDTO to save.
     * @param productVariationDTO the productVariationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariationDTO,
     * or with status {@code 400 (Bad Request)} if the productVariationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVariationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-variations/{id}")
    public Mono<ResponseEntity<ProductVariationDTO>> updateProductVariation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductVariationDTO productVariationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductVariation : {}, {}", id, productVariationDTO);
        if (productVariationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productVariationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productVariationService
                    .update(productVariationDTO)
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
     * {@code PATCH  /product-variations/:id} : Partial updates given fields of an existing productVariation, field will ignore if it is null
     *
     * @param id the id of the productVariationDTO to save.
     * @param productVariationDTO the productVariationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariationDTO,
     * or with status {@code 400 (Bad Request)} if the productVariationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productVariationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productVariationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-variations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductVariationDTO>> partialUpdateProductVariation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductVariationDTO productVariationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductVariation partially : {}, {}", id, productVariationDTO);
        if (productVariationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productVariationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductVariationDTO> result = productVariationService.partialUpdate(productVariationDTO);

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
     * {@code GET  /product-variations} : get all the productVariations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productVariations in body.
     */
    @GetMapping("/product-variations")
    public Mono<ResponseEntity<List<ProductVariationDTO>>> getAllProductVariations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductVariations");
        return productVariationService
            .countAll()
            .zipWith(productVariationService.findAll(pageable).collectList())
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
     * {@code GET  /product-variations/:id} : get the "id" productVariation.
     *
     * @param id the id of the productVariationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productVariationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-variations/{id}")
    public Mono<ResponseEntity<ProductVariationDTO>> getProductVariation(@PathVariable Long id) {
        log.debug("REST request to get ProductVariation : {}", id);
        Mono<ProductVariationDTO> productVariationDTO = productVariationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productVariationDTO);
    }

    /**
     * {@code DELETE  /product-variations/:id} : delete the "id" productVariation.
     *
     * @param id the id of the productVariationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-variations/{id}")
    public Mono<ResponseEntity<Void>> deleteProductVariation(@PathVariable Long id) {
        log.debug("REST request to delete ProductVariation : {}", id);
        return productVariationService
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
     * {@code SEARCH  /_search/product-variations?query=:query} : search for the productVariation corresponding
     * to the query.
     *
     * @param query the query of the productVariation search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-variations")
    public Mono<ResponseEntity<Flux<ProductVariationDTO>>> searchProductVariations(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductVariations for query {}", query);
        return productVariationService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productVariationService.search(query, pageable)));
    }
}
