package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Transaction;
import com.yalisoft.bister.repository.TransactionRepository;
import com.yalisoft.bister.repository.search.TransactionSearchRepository;
import com.yalisoft.bister.service.dto.TransactionDTO;
import com.yalisoft.bister.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionSearchRepository transactionSearchRepository;

    public TransactionService(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
    }

    /**
     * Save a transaction.
     *
     * @param transactionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TransactionDTO> save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);
        return transactionRepository
            .save(transactionMapper.toEntity(transactionDTO))
            .flatMap(transactionSearchRepository::save)
            .map(transactionMapper::toDto);
    }

    /**
     * Update a transaction.
     *
     * @param transactionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TransactionDTO> update(TransactionDTO transactionDTO) {
        log.debug("Request to update Transaction : {}", transactionDTO);
        return transactionRepository
            .save(transactionMapper.toEntity(transactionDTO))
            .flatMap(transactionSearchRepository::save)
            .map(transactionMapper::toDto);
    }

    /**
     * Partially update a transaction.
     *
     * @param transactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TransactionDTO> partialUpdate(TransactionDTO transactionDTO) {
        log.debug("Request to partially update Transaction : {}", transactionDTO);

        return transactionRepository
            .findById(transactionDTO.getId())
            .map(existingTransaction -> {
                transactionMapper.partialUpdate(existingTransaction, transactionDTO);

                return existingTransaction;
            })
            .flatMap(transactionRepository::save)
            .flatMap(savedTransaction -> {
                transactionSearchRepository.save(savedTransaction);

                return Mono.just(savedTransaction);
            })
            .map(transactionMapper::toDto);
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TransactionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAllBy(pageable).map(transactionMapper::toDto);
    }

    /**
     * Returns the number of transactions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return transactionRepository.count();
    }

    /**
     * Returns the number of transactions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return transactionSearchRepository.count();
    }

    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TransactionDTO> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        return transactionRepository.deleteById(id).then(transactionSearchRepository.deleteById(id));
    }

    /**
     * Search for the transaction corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TransactionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}", query);
        return transactionSearchRepository.search(query, pageable).map(transactionMapper::toDto);
    }
}
