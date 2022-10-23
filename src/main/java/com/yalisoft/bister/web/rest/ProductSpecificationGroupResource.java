package com.yalisoft.bister.web.rest;

import com.yalisoft.bister.repository.ProductSpecificationGroupRepository;
import com.yalisoft.bister.service.ProductSpecificationGroupService;
import com.yalisoft.bister.service.dto.ProductSpecificationGroupDTO;
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
 * REST controller for managing {@link com.yalisoft.bister.domain.ProductSpecificationGroup}.
 */
@RestController
@RequestMapping("/api")
public class ProductSpecificationGroupResource {

    private final Logger log = LoggerFactory.getLogger(ProductSpecificationGroupResource.class);

    private static final String ENTITY_NAME = "productSpecificationGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductSpecificationGroupService productSpecificationGroupService;

    private final ProductSpecificationGroupRepository productSpecificationGroupRepository;

    public ProductSpecificationGroupResource(
        ProductSpecificationGroupService productSpecificationGroupService,
        ProductSpecificationGroupRepository productSpecificationGroupRepository
    ) {
        this.productSpecificationGroupService = productSpecificationGroupService;
        this.productSpecificationGroupRepository = productSpecificationGroupRepository;
    }

    /**
     * {@code POST  /product-specification-groups} : Create a new productSpecificationGroup.
     *
     * @param productSpecificationGroupDTO the productSpecificationGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productSpecificationGroupDTO, or with status {@code 400 (Bad Request)} if the productSpecificationGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-specification-groups")
    public Mono<ResponseEntity<ProductSpecificationGroupDTO>> createProductSpecificationGroup(
        @Valid @RequestBody ProductSpecificationGroupDTO productSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProductSpecificationGroup : {}", productSpecificationGroupDTO);
        if (productSpecificationGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new productSpecificationGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productSpecificationGroupService
            .save(productSpecificationGroupDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-specification-groups/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-specification-groups/:id} : Updates an existing productSpecificationGroup.
     *
     * @param id the id of the productSpecificationGroupDTO to save.
     * @param productSpecificationGroupDTO the productSpecificationGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecificationGroupDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecificationGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productSpecificationGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-specification-groups/{id}")
    public Mono<ResponseEntity<ProductSpecificationGroupDTO>> updateProductSpecificationGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductSpecificationGroupDTO productSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductSpecificationGroup : {}, {}", id, productSpecificationGroupDTO);
        if (productSpecificationGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecificationGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productSpecificationGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productSpecificationGroupService
                    .update(productSpecificationGroupDTO)
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
     * {@code PATCH  /product-specification-groups/:id} : Partial updates given fields of an existing productSpecificationGroup, field will ignore if it is null
     *
     * @param id the id of the productSpecificationGroupDTO to save.
     * @param productSpecificationGroupDTO the productSpecificationGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecificationGroupDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecificationGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productSpecificationGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productSpecificationGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-specification-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductSpecificationGroupDTO>> partialUpdateProductSpecificationGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductSpecificationGroupDTO productSpecificationGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductSpecificationGroup partially : {}, {}", id, productSpecificationGroupDTO);
        if (productSpecificationGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecificationGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productSpecificationGroupRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductSpecificationGroupDTO> result = productSpecificationGroupService.partialUpdate(productSpecificationGroupDTO);

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
     * {@code GET  /product-specification-groups} : get all the productSpecificationGroups.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productSpecificationGroups in body.
     */
    @GetMapping("/product-specification-groups")
    public Mono<ResponseEntity<List<ProductSpecificationGroupDTO>>> getAllProductSpecificationGroups(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ProductSpecificationGroups");
        return productSpecificationGroupService
            .countAll()
            .zipWith(productSpecificationGroupService.findAll(pageable).collectList())
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
     * {@code GET  /product-specification-groups/:id} : get the "id" productSpecificationGroup.
     *
     * @param id the id of the productSpecificationGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productSpecificationGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-specification-groups/{id}")
    public Mono<ResponseEntity<ProductSpecificationGroupDTO>> getProductSpecificationGroup(@PathVariable Long id) {
        log.debug("REST request to get ProductSpecificationGroup : {}", id);
        Mono<ProductSpecificationGroupDTO> productSpecificationGroupDTO = productSpecificationGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productSpecificationGroupDTO);
    }

    /**
     * {@code DELETE  /product-specification-groups/:id} : delete the "id" productSpecificationGroup.
     *
     * @param id the id of the productSpecificationGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-specification-groups/{id}")
    public Mono<ResponseEntity<Void>> deleteProductSpecificationGroup(@PathVariable Long id) {
        log.debug("REST request to delete ProductSpecificationGroup : {}", id);
        return productSpecificationGroupService
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
     * {@code SEARCH  /_search/product-specification-groups?query=:query} : search for the productSpecificationGroup corresponding
     * to the query.
     *
     * @param query the query of the productSpecificationGroup search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-specification-groups")
    public Mono<ResponseEntity<Flux<ProductSpecificationGroupDTO>>> searchProductSpecificationGroups(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ProductSpecificationGroups for query {}", query);
        return productSpecificationGroupService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(productSpecificationGroupService.search(query, pageable)));
    }
}
