package com.yalisoft.bister.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.repository.AgentRepository;
import com.yalisoft.bister.repository.search.AgentSearchRepository;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.mapper.AgentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Agent}.
 */
@Service
@Transactional
public class AgentService {

    private final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final AgentRepository agentRepository;

    private final AgentMapper agentMapper;

    private final AgentSearchRepository agentSearchRepository;

    public AgentService(AgentRepository agentRepository, AgentMapper agentMapper, AgentSearchRepository agentSearchRepository) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
        this.agentSearchRepository = agentSearchRepository;
    }

    /**
     * Save a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AgentDTO> save(AgentDTO agentDTO) {
        log.debug("Request to save Agent : {}", agentDTO);
        return agentRepository.save(agentMapper.toEntity(agentDTO)).flatMap(agentSearchRepository::save).map(agentMapper::toDto);
    }

    /**
     * Update a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AgentDTO> update(AgentDTO agentDTO) {
        log.debug("Request to update Agent : {}", agentDTO);
        return agentRepository.save(agentMapper.toEntity(agentDTO)).flatMap(agentSearchRepository::save).map(agentMapper::toDto);
    }

    /**
     * Partially update a agent.
     *
     * @param agentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AgentDTO> partialUpdate(AgentDTO agentDTO) {
        log.debug("Request to partially update Agent : {}", agentDTO);

        return agentRepository
            .findById(agentDTO.getId())
            .map(existingAgent -> {
                agentMapper.partialUpdate(existingAgent, agentDTO);

                return existingAgent;
            })
            .flatMap(agentRepository::save)
            .flatMap(savedAgent -> {
                agentSearchRepository.save(savedAgent);

                return Mono.just(savedAgent);
            })
            .map(agentMapper::toDto);
    }

    /**
     * Get all the agents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AgentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Agents");
        return agentRepository.findAllBy(pageable).map(agentMapper::toDto);
    }

    /**
     * Get all the agents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<AgentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return agentRepository.findAllWithEagerRelationships(pageable).map(agentMapper::toDto);
    }

    /**
     * Returns the number of agents available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return agentRepository.count();
    }

    /**
     * Returns the number of agents available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return agentSearchRepository.count();
    }

    /**
     * Get one agent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AgentDTO> findOne(Long id) {
        log.debug("Request to get Agent : {}", id);
        return agentRepository.findOneWithEagerRelationships(id).map(agentMapper::toDto);
    }

    /**
     * Delete the agent by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Agent : {}", id);
        return agentRepository.deleteById(id).then(agentSearchRepository.deleteById(id));
    }

    /**
     * Search for the agent corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AgentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Agents for query {}", query);
        return agentSearchRepository.search(query, pageable).map(agentMapper::toDto);
    }
}
