package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.repository.PurchaseOrderRepository;
import com.yalisoft.bister.repository.search.PurchaseOrderSearchRepository;
import com.yalisoft.bister.security.AuthoritiesConstants;
import com.yalisoft.bister.security.SecurityUtils;
import com.yalisoft.bister.service.dto.PurchaseOrderDTO;
import com.yalisoft.bister.service.mapper.PurchaseOrderMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link PurchaseOrder}.
 */
@Service
@Transactional
public class PurchaseOrderService {

    private final Logger log = LoggerFactory.getLogger(PurchaseOrderService.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final PurchaseOrderSearchRepository purchaseOrderSearchRepository;

    public PurchaseOrderService(
        PurchaseOrderRepository purchaseOrderRepository,
        PurchaseOrderMapper purchaseOrderMapper,
        PurchaseOrderSearchRepository purchaseOrderSearchRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderSearchRepository = purchaseOrderSearchRepository;
    }

    /**
     * Save a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PurchaseOrderDTO> save(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        return purchaseOrderRepository
            .save(purchaseOrderMapper.toEntity(purchaseOrderDTO))
            .flatMap(purchaseOrderSearchRepository::save)
            .map(purchaseOrderMapper::toDto);
    }

    /**
     * Update a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PurchaseOrderDTO> update(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);
        return purchaseOrderRepository
            .save(purchaseOrderMapper.toEntity(purchaseOrderDTO))
            .flatMap(purchaseOrderSearchRepository::save)
            .map(purchaseOrderMapper::toDto);
    }

    /**
     * Partially update a purchaseOrder.
     *
     * @param purchaseOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PurchaseOrderDTO> partialUpdate(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to partially update PurchaseOrder : {}", purchaseOrderDTO);

        return purchaseOrderRepository
            .findById(purchaseOrderDTO.getId())
            .map(existingPurchaseOrder -> {
                purchaseOrderMapper.partialUpdate(existingPurchaseOrder, purchaseOrderDTO);

                return existingPurchaseOrder;
            })
            .flatMap(purchaseOrderRepository::save)
            .flatMap(savedPurchaseOrder -> {
                purchaseOrderSearchRepository.save(savedPurchaseOrder);

                return Mono.just(savedPurchaseOrder);
            })
            .map(purchaseOrderMapper::toDto);
    }

    /**
     * Get all the purchaseOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PurchaseOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseOrders");
        return SecurityUtils
            .hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (isAdmin) {
                    return purchaseOrderRepository.findAllBy(pageable).map(purchaseOrderMapper::toDto);
                } else {
                    return SecurityUtils
                        .getCurrentUserLogin()
                        .flatMapMany(name -> {
                            System.out.println("$$$$$$$" + name);
                            return purchaseOrderRepository.findAllByCustomerUserLogin(name, pageable).map(purchaseOrderMapper::toDto);
                        });
                }
            });
    }

    /**
     * Get all the purchaseOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<PurchaseOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return purchaseOrderRepository.findAllWithEagerRelationships(pageable).map(purchaseOrderMapper::toDto);
    }

    /**
     *  Get all the purchaseOrders where Invoice is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PurchaseOrderDTO> findAllWhereInvoiceIsNull() {
        log.debug("Request to get all purchaseOrders where Invoice is null");
        return purchaseOrderRepository.findAllWhereInvoiceIsNull().map(purchaseOrderMapper::toDto);
    }

    /**
     * Returns the number of purchaseOrders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return purchaseOrderRepository.count();
    }

    /**
     * Returns the number of purchaseOrders available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return purchaseOrderSearchRepository.count();
    }

    /**
     * Get one purchaseOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PurchaseOrderDTO> findOne(Long id) {
        log.debug("Request to get PurchaseOrder : {}", id);
        return purchaseOrderRepository.findOneWithEagerRelationships(id).map(purchaseOrderMapper::toDto);
    }

    /**
     * Delete the purchaseOrder by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PurchaseOrder : {}", id);
        return purchaseOrderRepository.deleteById(id).then(purchaseOrderSearchRepository.deleteById(id));
    }

    /**
     * Search for the purchaseOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PurchaseOrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PurchaseOrders for query {}", query);
        return purchaseOrderSearchRepository.search(query, pageable).map(purchaseOrderMapper::toDto);
    }
}
