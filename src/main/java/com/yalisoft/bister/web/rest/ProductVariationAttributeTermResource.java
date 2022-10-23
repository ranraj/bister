package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductVariationAttributeTermRepository;
import com.yalisoft.bister.service.ProductVariationAttributeTermService;
import com.yalisoft.bister.service.dto.ProductVariationAttributeTermDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductVariationAttributeTerm}.
 */
@RestController
@RequestMapping("/api")
public class ProductVariationAttributeTermResource {

    private final Logger log = LoggerFactory.getLogger(ProductVariationAttributeTermResource.class);

    private static final String ENTITY_NAME = "productVariationAttributeTerm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductVariationAttributeTermService productVariationAttributeTermService;

    private final ProductVariationAttributeTermRepository productVariationAttributeTermRepository;

    public ProductVariationAttributeTermResource(
        ProductVariationAttributeTermService productVariationAttributeTermService,
        ProductVariationAttributeTermRepository productVariationAttributeTermRepository
    ) {
        this.productVariationAttributeTermService = productVariationAttributeTermService;
        this.productVariationAttributeTermRepository = productVariationAttributeTermRepository;
    }

    /**
     * {@code POST  /product-variation-attribute-terms} : Create a new productVariationAttributeTerm.
     *
     * @param productVariationAttributeTermDTO the productVariationAttributeTermDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productVariationAttributeTermDTO, or with status {@code 400 (Bad Request)} if the productVariationAttributeTerm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-variation-attribute-terms")
    public Mono<ResponseEntity<ProductVariationAttributeTermDTO>> createProductVariationAttributeTerm(
        @Valid @RequestBody ProductVariationAttributeTermDTO productVariationAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProductVariationAttributeTerm : {}", productVariationAttributeTermDTO);
        if (productVariationAttributeTermDTO.getId() != null) {
            throw new BadRequestAlertException("A new productVariationAttributeTerm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productVariationAttributeTermService
            .save(productVariationAttributeTermDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-variation-attribute-terms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-variation-attribute-terms/:id} : Updates an existing productVariationAttributeTerm.
     *
     * @param id the id of the productVariationAttributeTermDTO to save.
     * @param productVariationAttributeTermDTO the productVariationAttributeTermDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariationAttributeTermDTO,
     * or with status {@code 400 (Bad Request)} if the productVariationAttributeTermDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVariationAttributeTermDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-variation-attribute-terms/{id}")
    public Mono<ResponseEntity<ProductVariationAttributeTermDTO>> updateProductVariationAttributeTerm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductVariationAttributeTermDTO productVariationAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductVariationAttributeTerm : {}, {}", id, productVariationAttributeTermDTO);
        if (productVariationAttributeTermDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariationAttributeTermDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productVariationAttributeTermRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productVariationAttributeTermService
                    .update(productVariationAttributeTermDTO)
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
     * {@code PATCH  /product-variation-attribute-terms/:id} : Partial updates given fields of an existing productVariationAttributeTerm, field will ignore if it is null
     *
     * @param id the id of the productVariationAttributeTermDTO to save.
     * @param productVariationAttributeTermDTO the productVariationAttributeTermDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariationAttributeTermDTO,
     * or with status {@code 400 (Bad Request)} if the productVariationAttributeTermDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productVariationAttributeTermDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productVariationAttributeTermDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-variation-attribute-terms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductVariationAttributeTermDTO>> partialUpdateProductVariationAttributeTerm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductVariationAttributeTermDTO productVariationAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductVariationAttributeTerm partially : {}, {}", id, productVariationAttributeTermDTO);
        if (productVariationAttributeTermDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariationAttributeTermDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productVariationAttributeTermRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductVariationAttributeTermDTO> result = productVariationAttributeTermService.partialUpdate(
                    productVariationAttributeTermDTO
                );

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
     * {@code GET  /product-variation-attribute-terms} : get all the productVariationAttributeTerms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productVariationAttributeTerms in body.
     */
    @GetMapping("/product-variation-attribute-terms")
    public Mono<ResponseEntity<List<ProductVariationAttributeTermDTO>>> getAllProductVariationAttributeTerms(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductVariationAttributeTerms");
        return productVariationAttributeTermService
            .countAll()
            .zipWith(productVariationAttributeTermService.findAll(pageable).collectList())
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
     * {@code GET  /product-variation-attribute-terms/:id} : get the "id" productVariationAttributeTerm.
     *
     * @param id the id of the productVariationAttributeTermDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productVariationAttributeTermDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-variation-attribute-terms/{id}")
    public Mono<ResponseEntity<ProductVariationAttributeTermDTO>> getProductVariationAttributeTerm(@PathVariable Long id) {
        log.debug("REST request to get ProductVariationAttributeTerm : {}", id);
        Mono<ProductVariationAttributeTermDTO> productVariationAttributeTermDTO = productVariationAttributeTermService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productVariationAttributeTermDTO);
    }

    /**
     * {@code DELETE  /product-variation-attribute-terms/:id} : delete the "id" productVariationAttributeTerm.
     *
     * @param id the id of the productVariationAttributeTermDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-variation-attribute-terms/{id}")
    public Mono<ResponseEntity<Void>> deleteProductVariationAttributeTerm(@PathVariable Long id) {
        log.debug("REST request to delete ProductVariationAttributeTerm : {}", id);
        return productVariationAttributeTermService
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
     * {@code SEARCH  /_search/product-variation-attribute-terms?query=:query} : search for the productVariationAttributeTerm corresponding
     * to the query.
     *
     * @param query the query of the productVariationAttributeTerm search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-variation-attribute-terms")
    public Mono<ResponseEntity<Flux<ProductVariationAttributeTermDTO>>> searchProductVariationAttributeTerms(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductVariationAttributeTerms for query {}", query);
        return productVariationAttributeTermService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productVariationAttributeTermService.search(query, pageable)));
    }
}
