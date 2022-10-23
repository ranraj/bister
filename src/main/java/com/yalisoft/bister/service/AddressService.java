package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.repository.AddressRepository;
import com.yalisoft.bister.repository.search.AddressSearchRepository;
import com.yalisoft.bister.service.dto.AddressDTO;
import com.yalisoft.bister.service.mapper.AddressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Address}.
 */
@Service
@Transactional
public class AddressService {

    private final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    private final AddressSearchRepository addressSearchRepository;

    public AddressService(
        AddressRepository addressRepository,
        AddressMapper addressMapper,
        AddressSearchRepository addressSearchRepository
    ) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.addressSearchRepository = addressSearchRepository;
    }

    /**
     * Save a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> save(AddressDTO addressDTO) {
        log.debug("Request to save Address : {}", addressDTO);
        return addressRepository.save(addressMapper.toEntity(addressDTO)).flatMap(addressSearchRepository::save).map(addressMapper::toDto);
    }

    /**
     * Update a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> update(AddressDTO addressDTO) {
        log.debug("Request to update Address : {}", addressDTO);
        return addressRepository.save(addressMapper.toEntity(addressDTO)).flatMap(addressSearchRepository::save).map(addressMapper::toDto);
    }

    /**
     * Partially update a address.
     *
     * @param addressDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> partialUpdate(AddressDTO addressDTO) {
        log.debug("Request to partially update Address : {}", addressDTO);

        return addressRepository
            .findById(addressDTO.getId())
            .map(existingAddress -> {
                addressMapper.partialUpdate(existingAddress, addressDTO);

                return existingAddress;
            })
            .flatMap(addressRepository::save)
            .flatMap(savedAddress -> {
                addressSearchRepository.save(savedAddress);

                return Mono.just(savedAddress);
            })
            .map(addressMapper::toDto);
    }

    /**
     * Get all the addresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AddressDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Addresses");
        return addressRepository.findAllBy(pageable).map(addressMapper::toDto);
    }

    /**
     * Returns the number of addresses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return addressRepository.count();
    }

    /**
     * Returns the number of addresses available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return addressSearchRepository.count();
    }

    /**
     * Get one address by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AddressDTO> findOne(Long id) {
        log.debug("Request to get Address : {}", id);
        return addressRepository.findById(id).map(addressMapper::toDto);
    }

    /**
     * Delete the address by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Address : {}", id);
        return addressRepository.deleteById(id).then(addressSearchRepository.deleteById(id));
    }

    /**
     * Search for the address corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AddressDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Addresses for query {}", query);
        return addressSearchRepository.search(query, pageable).map(addressMapper::toDto);
    }
}
