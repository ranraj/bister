package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Invoice;
import com.yalisoft.bister.repository.InvoiceRepository;
import com.yalisoft.bister.repository.search.InvoiceSearchRepository;
import com.yalisoft.bister.service.dto.InvoiceDTO;
import com.yalisoft.bister.service.mapper.InvoiceMapper;
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
 * Service Implementation for managing {@link Invoice}.
 */
@Service
@Transactional
public class InvoiceService {

    private final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;

    private final InvoiceMapper invoiceMapper;

    private final InvoiceSearchRepository invoiceSearchRepository;

    public InvoiceService(
        InvoiceRepository invoiceRepository,
        InvoiceMapper invoiceMapper,
        InvoiceSearchRepository invoiceSearchRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.invoiceSearchRepository = invoiceSearchRepository;
    }

    /**
     * Save a invoice.
     *
     * @param invoiceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        log.debug("Request to save Invoice : {}", invoiceDTO);
        return invoiceRepository.save(invoiceMapper.toEntity(invoiceDTO)).flatMap(invoiceSearchRepository::save).map(invoiceMapper::toDto);
    }

    /**
     * Update a invoice.
     *
     * @param invoiceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InvoiceDTO> update(InvoiceDTO invoiceDTO) {
        log.debug("Request to update Invoice : {}", invoiceDTO);
        return invoiceRepository.save(invoiceMapper.toEntity(invoiceDTO)).flatMap(invoiceSearchRepository::save).map(invoiceMapper::toDto);
    }

    /**
     * Partially update a invoice.
     *
     * @param invoiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<InvoiceDTO> partialUpdate(InvoiceDTO invoiceDTO) {
        log.debug("Request to partially update Invoice : {}", invoiceDTO);

        return invoiceRepository
            .findById(invoiceDTO.getId())
            .map(existingInvoice -> {
                invoiceMapper.partialUpdate(existingInvoice, invoiceDTO);

                return existingInvoice;
            })
            .flatMap(invoiceRepository::save)
            .flatMap(savedInvoice -> {
                invoiceSearchRepository.save(savedInvoice);

                return Mono.just(savedInvoice);
            })
            .map(invoiceMapper::toDto);
    }

    /**
     * Get all the invoices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InvoiceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Invoices");
        return invoiceRepository.findAllBy(pageable).map(invoiceMapper::toDto);
    }

    /**
     *  Get all the invoices where PaymentSchedule is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InvoiceDTO> findAllWherePaymentScheduleIsNull() {
        log.debug("Request to get all invoices where PaymentSchedule is null");
        return invoiceRepository.findAllWherePaymentScheduleIsNull().map(invoiceMapper::toDto);
    }

    /**
     * Returns the number of invoices available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return invoiceRepository.count();
    }

    /**
     * Returns the number of invoices available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return invoiceSearchRepository.count();
    }

    /**
     * Get one invoice by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<InvoiceDTO> findOne(Long id) {
        log.debug("Request to get Invoice : {}", id);
        return invoiceRepository.findById(id).map(invoiceMapper::toDto);
    }

    /**
     * Delete the invoice by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Invoice : {}", id);
        return invoiceRepository.deleteById(id).then(invoiceSearchRepository.deleteById(id));
    }

    /**
     * Search for the invoice corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InvoiceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Invoices for query {}", query);
        return invoiceSearchRepository.search(query, pageable).map(invoiceMapper::toDto);
    }
}
