package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.repository.PaymentScheduleRepository;
import com.yalisoft.bister.repository.search.PaymentScheduleSearchRepository;
import com.yalisoft.bister.service.dto.PaymentScheduleDTO;
import com.yalisoft.bister.service.mapper.PaymentScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link PaymentSchedule}.
 */
@Service
@Transactional
public class PaymentScheduleService {

    private final Logger log = LoggerFactory.getLogger(PaymentScheduleService.class);

    private final PaymentScheduleRepository paymentScheduleRepository;

    private final PaymentScheduleMapper paymentScheduleMapper;

    private final PaymentScheduleSearchRepository paymentScheduleSearchRepository;

    public PaymentScheduleService(
        PaymentScheduleRepository paymentScheduleRepository,
        PaymentScheduleMapper paymentScheduleMapper,
        PaymentScheduleSearchRepository paymentScheduleSearchRepository
    ) {
        this.paymentScheduleRepository = paymentScheduleRepository;
        this.paymentScheduleMapper = paymentScheduleMapper;
        this.paymentScheduleSearchRepository = paymentScheduleSearchRepository;
    }

    /**
     * Save a paymentSchedule.
     *
     * @param paymentScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentScheduleDTO> save(PaymentScheduleDTO paymentScheduleDTO) {
        log.debug("Request to save PaymentSchedule : {}", paymentScheduleDTO);
        return paymentScheduleRepository
            .save(paymentScheduleMapper.toEntity(paymentScheduleDTO))
            .flatMap(paymentScheduleSearchRepository::save)
            .map(paymentScheduleMapper::toDto);
    }

    /**
     * Update a paymentSchedule.
     *
     * @param paymentScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentScheduleDTO> update(PaymentScheduleDTO paymentScheduleDTO) {
        log.debug("Request to update PaymentSchedule : {}", paymentScheduleDTO);
        return paymentScheduleRepository
            .save(paymentScheduleMapper.toEntity(paymentScheduleDTO))
            .flatMap(paymentScheduleSearchRepository::save)
            .map(paymentScheduleMapper::toDto);
    }

    /**
     * Partially update a paymentSchedule.
     *
     * @param paymentScheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentScheduleDTO> partialUpdate(PaymentScheduleDTO paymentScheduleDTO) {
        log.debug("Request to partially update PaymentSchedule : {}", paymentScheduleDTO);

        return paymentScheduleRepository
            .findById(paymentScheduleDTO.getId())
            .map(existingPaymentSchedule -> {
                paymentScheduleMapper.partialUpdate(existingPaymentSchedule, paymentScheduleDTO);

                return existingPaymentSchedule;
            })
            .flatMap(paymentScheduleRepository::save)
            .flatMap(savedPaymentSchedule -> {
                paymentScheduleSearchRepository.save(savedPaymentSchedule);

                return Mono.just(savedPaymentSchedule);
            })
            .map(paymentScheduleMapper::toDto);
    }

    /**
     * Get all the paymentSchedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentScheduleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PaymentSchedules");
        return paymentScheduleRepository.findAllBy(pageable).map(paymentScheduleMapper::toDto);
    }

    /**
     * Returns the number of paymentSchedules available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentScheduleRepository.count();
    }

    /**
     * Returns the number of paymentSchedules available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return paymentScheduleSearchRepository.count();
    }

    /**
     * Get one paymentSchedule by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentScheduleDTO> findOne(Long id) {
        log.debug("Request to get PaymentSchedule : {}", id);
        return paymentScheduleRepository.findById(id).map(paymentScheduleMapper::toDto);
    }

    /**
     * Delete the paymentSchedule by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PaymentSchedule : {}", id);
        return paymentScheduleRepository.deleteById(id).then(paymentScheduleSearchRepository.deleteById(id));
    }

    /**
     * Search for the paymentSchedule corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentScheduleDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PaymentSchedules for query {}", query);
        return paymentScheduleSearchRepository.search(query, pageable).map(paymentScheduleMapper::toDto);
    }
}
