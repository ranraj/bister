package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductAttributeRepository;
import com.yalisoft.bister.service.ProductAttributeService;
import com.yalisoft.bister.service.dto.ProductAttributeDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductAttribute}.
 */
@RestController
@RequestMapping("/api")
public class ProductAttributeResource {

    private final Logger log = LoggerFactory.getLogger(ProductAttributeResource.class);

    private static final String ENTITY_NAME = "productAttribute";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductAttributeService productAttributeService;

    private final ProductAttributeRepository productAttributeRepository;

    public ProductAttributeResource(
        ProductAttributeService productAttributeService,
        ProductAttributeRepository productAttributeRepository
    ) {
        this.productAttributeService = productAttributeService;
        this.productAttributeRepository = productAttributeRepository;
    }

    /**
     * {@code POST  /product-attributes} : Create a new productAttribute.
     *
     * @param productAttributeDTO the productAttributeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productAttributeDTO, or with status {@code 400 (Bad Request)} if the productAttribute has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-attributes")
    public Mono<ResponseEntity<ProductAttributeDTO>> createProductAttribute(@Valid @RequestBody ProductAttributeDTO productAttributeDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductAttribute : {}", productAttributeDTO);
        if (productAttributeDTO.getId() != null) {
            throw new BadRequestAlertException("A new productAttribute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productAttributeService
            .save(productAttributeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-attributes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-attributes/:id} : Updates an existing productAttribute.
     *
     * @param id the id of the productAttributeDTO to save.
     * @param productAttributeDTO the productAttributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAttributeDTO,
     * or with status {@code 400 (Bad Request)} if the productAttributeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productAttributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-attributes/{id}")
    public Mono<ResponseEntity<ProductAttributeDTO>> updateProductAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductAttributeDTO productAttributeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductAttribute : {}, {}", id, productAttributeDTO);
        if (productAttributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAttributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productAttributeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productAttributeService
                    .update(productAttributeDTO)
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
     * {@code PATCH  /product-attributes/:id} : Partial updates given fields of an existing productAttribute, field will ignore if it is null
     *
     * @param id the id of the productAttributeDTO to save.
     * @param productAttributeDTO the productAttributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAttributeDTO,
     * or with status {@code 400 (Bad Request)} if the productAttributeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productAttributeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productAttributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-attributes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductAttributeDTO>> partialUpdateProductAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductAttributeDTO productAttributeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductAttribute partially : {}, {}", id, productAttributeDTO);
        if (productAttributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAttributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productAttributeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductAttributeDTO> result = productAttributeService.partialUpdate(productAttributeDTO);

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
     * {@code GET  /product-attributes} : get all the productAttributes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productAttributes in body.
     */
    @GetMapping("/product-attributes")
    public Mono<ResponseEntity<List<ProductAttributeDTO>>> getAllProductAttributes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of ProductAttributes");
        return productAttributeService
            .countAll()
            .zipWith(productAttributeService.findAll(pageable).collectList())
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
     * {@code GET  /product-attributes/:id} : get the "id" productAttribute.
     *
     * @param id the id of the productAttributeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productAttributeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-attributes/{id}")
    public Mono<ResponseEntity<ProductAttributeDTO>> getProductAttribute(@PathVariable Long id) {
        log.debug("REST request to get ProductAttribute : {}", id);
        Mono<ProductAttributeDTO> productAttributeDTO = productAttributeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productAttributeDTO);
    }

    /**
     * {@code DELETE  /product-attributes/:id} : delete the "id" productAttribute.
     *
     * @param id the id of the productAttributeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-attributes/{id}")
    public Mono<ResponseEntity<Void>> deleteProductAttribute(@PathVariable Long id) {
        log.debug("REST request to delete ProductAttribute : {}", id);
        return productAttributeService
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
     * {@code SEARCH  /_search/product-attributes?query=:query} : search for the productAttribute corresponding
     * to the query.
     *
     * @param query the query of the productAttribute search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-attributes")
    public Mono<ResponseEntity<Flux<ProductAttributeDTO>>> searchProductAttributes(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductAttributes for query {}", query);
        return productAttributeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productAttributeService.search(query, pageable)));
    }
}
