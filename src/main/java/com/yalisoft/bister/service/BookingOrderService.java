package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.BookingOrder;
import com.yalisoft.bister.repository.BookingOrderRepository;
import com.yalisoft.bister.repository.search.BookingOrderSearchRepository;
import com.yalisoft.bister.service.dto.BookingOrderDTO;
import com.yalisoft.bister.service.mapper.BookingOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link BookingOrder}.
 */
@Service
@Transactional
public class BookingOrderService {

    private final Logger log = LoggerFactory.getLogger(BookingOrderService.class);

    private final BookingOrderRepository bookingOrderRepository;

    private final BookingOrderMapper bookingOrderMapper;

    private final BookingOrderSearchRepository bookingOrderSearchRepository;

    public BookingOrderService(
        BookingOrderRepository bookingOrderRepository,
        BookingOrderMapper bookingOrderMapper,
        BookingOrderSearchRepository bookingOrderSearchRepository
    ) {
        this.bookingOrderRepository = bookingOrderRepository;
        this.bookingOrderMapper = bookingOrderMapper;
        this.bookingOrderSearchRepository = bookingOrderSearchRepository;
    }

    /**
     * Save a bookingOrder.
     *
     * @param bookingOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BookingOrderDTO> save(BookingOrderDTO bookingOrderDTO) {
        log.debug("Request to save BookingOrder : {}", bookingOrderDTO);
        return bookingOrderRepository
            .save(bookingOrderMapper.toEntity(bookingOrderDTO))
            .flatMap(bookingOrderSearchRepository::save)
            .map(bookingOrderMapper::toDto);
    }

    /**
     * Update a bookingOrder.
     *
     * @param bookingOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BookingOrderDTO> update(BookingOrderDTO bookingOrderDTO) {
        log.debug("Request to update BookingOrder : {}", bookingOrderDTO);
        return bookingOrderRepository
            .save(bookingOrderMapper.toEntity(bookingOrderDTO))
            .flatMap(bookingOrderSearchRepository::save)
            .map(bookingOrderMapper::toDto);
    }

    /**
     * Partially update a bookingOrder.
     *
     * @param bookingOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BookingOrderDTO> partialUpdate(BookingOrderDTO bookingOrderDTO) {
        log.debug("Request to partially update BookingOrder : {}", bookingOrderDTO);

        return bookingOrderRepository
            .findById(bookingOrderDTO.getId())
            .map(existingBookingOrder -> {
                bookingOrderMapper.partialUpdate(existingBookingOrder, bookingOrderDTO);

                return existingBookingOrder;
            })
            .flatMap(bookingOrderRepository::save)
            .flatMap(savedBookingOrder -> {
                bookingOrderSearchRepository.save(savedBookingOrder);

                return Mono.just(savedBookingOrder);
            })
            .map(bookingOrderMapper::toDto);
    }

    /**
     * Get all the bookingOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BookingOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BookingOrders");
        return bookingOrderRepository.findAllBy(pageable).map(bookingOrderMapper::toDto);
    }

    /**
     * Get all the bookingOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<BookingOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookingOrderRepository.findAllWithEagerRelationships(pageable).map(bookingOrderMapper::toDto);
    }

    /**
     * Returns the number of bookingOrders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return bookingOrderRepository.count();
    }

    /**
     * Returns the number of bookingOrders available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return bookingOrderSearchRepository.count();
    }

    /**
     * Get one bookingOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BookingOrderDTO> findOne(Long id) {
        log.debug("Request to get BookingOrder : {}", id);
        return bookingOrderRepository.findOneWithEagerRelationships(id).map(bookingOrderMapper::toDto);
    }

    /**
     * Delete the bookingOrder by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete BookingOrder : {}", id);
        return bookingOrderRepository.deleteById(id).then(bookingOrderSearchRepository.deleteById(id));
    }

    /**
     * Search for the bookingOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BookingOrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BookingOrders for query {}", query);
        return bookingOrderSearchRepository.search(query, pageable).map(bookingOrderMapper::toDto);
    }
}
