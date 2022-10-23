package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductAttributeTermRepository;
import com.yalisoft.bister.service.ProductAttributeTermService;
import com.yalisoft.bister.service.dto.ProductAttributeTermDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductAttributeTerm}.
 */
@RestController
@RequestMapping("/api")
public class ProductAttributeTermResource {

    private final Logger log = LoggerFactory.getLogger(ProductAttributeTermResource.class);

    private static final String ENTITY_NAME = "productAttributeTerm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductAttributeTermService productAttributeTermService;

    private final ProductAttributeTermRepository productAttributeTermRepository;

    public ProductAttributeTermResource(
        ProductAttributeTermService productAttributeTermService,
        ProductAttributeTermRepository productAttributeTermRepository
    ) {
        this.productAttributeTermService = productAttributeTermService;
        this.productAttributeTermRepository = productAttributeTermRepository;
    }

    /**
     * {@code POST  /product-attribute-terms} : Create a new productAttributeTerm.
     *
     * @param productAttributeTermDTO the productAttributeTermDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productAttributeTermDTO, or with status {@code 400 (Bad Request)} if the productAttributeTerm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-attribute-terms")
    public Mono<ResponseEntity<ProductAttributeTermDTO>> createProductAttributeTerm(
        @Valid @RequestBody ProductAttributeTermDTO productAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProductAttributeTerm : {}", productAttributeTermDTO);
        if (productAttributeTermDTO.getId() != null) {
            throw new BadRequestAlertException("A new productAttributeTerm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productAttributeTermService
            .save(productAttributeTermDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-attribute-terms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-attribute-terms/:id} : Updates an existing productAttributeTerm.
     *
     * @param id the id of the productAttributeTermDTO to save.
     * @param productAttributeTermDTO the productAttributeTermDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAttributeTermDTO,
     * or with status {@code 400 (Bad Request)} if the productAttributeTermDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productAttributeTermDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-attribute-terms/{id}")
    public Mono<ResponseEntity<ProductAttributeTermDTO>> updateProductAttributeTerm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductAttributeTermDTO productAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductAttributeTerm : {}, {}", id, productAttributeTermDTO);
        if (productAttributeTermDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAttributeTermDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productAttributeTermRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productAttributeTermService
                    .update(productAttributeTermDTO)
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
     * {@code PATCH  /product-attribute-terms/:id} : Partial updates given fields of an existing productAttributeTerm, field will ignore if it is null
     *
     * @param id the id of the productAttributeTermDTO to save.
     * @param productAttributeTermDTO the productAttributeTermDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAttributeTermDTO,
     * or with status {@code 400 (Bad Request)} if the productAttributeTermDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productAttributeTermDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productAttributeTermDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-attribute-terms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductAttributeTermDTO>> partialUpdateProductAttributeTerm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductAttributeTermDTO productAttributeTermDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductAttributeTerm partially : {}, {}", id, productAttributeTermDTO);
        if (productAttributeTermDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAttributeTermDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productAttributeTermRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductAttributeTermDTO> result = productAttributeTermService.partialUpdate(productAttributeTermDTO);

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
     * {@code GET  /product-attribute-terms} : get all the productAttributeTerms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productAttributeTerms in body.
     */
    @GetMapping("/product-attribute-terms")
    public Mono<ResponseEntity<List<ProductAttributeTermDTO>>> getAllProductAttributeTerms(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductAttributeTerms");
        return productAttributeTermService
            .countAll()
            .zipWith(productAttributeTermService.findAll(pageable).collectList())
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
     * {@code GET  /product-attribute-terms/:id} : get the "id" productAttributeTerm.
     *
     * @param id the id of the productAttributeTermDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productAttributeTermDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-attribute-terms/{id}")
    public Mono<ResponseEntity<ProductAttributeTermDTO>> getProductAttributeTerm(@PathVariable Long id) {
        log.debug("REST request to get ProductAttributeTerm : {}", id);
        Mono<ProductAttributeTermDTO> productAttributeTermDTO = productAttributeTermService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productAttributeTermDTO);
    }

    /**
     * {@code DELETE  /product-attribute-terms/:id} : delete the "id" productAttributeTerm.
     *
     * @param id the id of the productAttributeTermDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-attribute-terms/{id}")
    public Mono<ResponseEntity<Void>> deleteProductAttributeTerm(@PathVariable Long id) {
        log.debug("REST request to delete ProductAttributeTerm : {}", id);
        return productAttributeTermService
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
     * {@code SEARCH  /_search/product-attribute-terms?query=:query} : search for the productAttributeTerm corresponding
     * to the query.
     *
     * @param query the query of the productAttributeTerm search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-attribute-terms")
    public Mono<ResponseEntity<Flux<ProductAttributeTermDTO>>> searchProductAttributeTerms(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductAttributeTerms for query {}", query);
        return productAttributeTermService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productAttributeTermService.search(query, pageable)));
    }
}
