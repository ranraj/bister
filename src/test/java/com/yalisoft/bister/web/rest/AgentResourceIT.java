package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.domain.enumeration.AgentType;
import com.yalisoft.bister.repository.AgentRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.AgentSearchRepository;
import com.yalisoft.bister.service.AgentService;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.mapper.AgentMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link AgentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AgentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_AVATAR_URL = "AAAAAAAAAA";
    private static final String UPDATED_AVATAR_URL = "BBBBBBBBBB";

    private static final AgentType DEFAULT_AGENT_TYPE = AgentType.ENGINEER;
    private static final AgentType UPDATED_AGENT_TYPE = AgentType.SUPPORT;

    private static final String ENTITY_API_URL = "/api/agents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/agents";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AgentRepository agentRepository;

    @Mock
    private AgentRepository agentRepositoryMock;

    @Autowired
    private AgentMapper agentMapper;

    @Mock
    private AgentService agentServiceMock;

    @Autowired
    private AgentSearchRepository agentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Agent agent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createEntity(EntityManager em) {
        Agent agent = new Agent()
            .name(DEFAULT_NAME)
            .contactNumber(DEFAULT_CONTACT_NUMBER)
            .avatarUrl(DEFAULT_AVATAR_URL)
            .agentType(DEFAULT_AGENT_TYPE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        agent.setUser(user);
        // Add required entity
        Facility facility;
        facility = em.insert(FacilityResourceIT.createEntity(em)).block();
        agent.setFacility(facility);
        return agent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createUpdatedEntity(EntityManager em) {
        Agent agent = new Agent()
            .name(UPDATED_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .avatarUrl(UPDATED_AVATAR_URL)
            .agentType(UPDATED_AGENT_TYPE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        agent.setUser(user);
        // Add required entity
        Facility facility;
        facility = em.insert(FacilityResourceIT.createUpdatedEntity(em)).block();
        agent.setFacility(facility);
        return agent;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Agent.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
        FacilityResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        agentSearchRepository.deleteAll().block();
        assertThat(agentSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        agent = createEntity(em);
    }

    @Test
    void createAgent() throws Exception {
        int databaseSizeBeforeCreate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Agent testAgent = agentList.get(agentList.size() - 1);
        assertThat(testAgent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAgent.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testAgent.getAvatarUrl()).isEqualTo(DEFAULT_AVATAR_URL);
        assertThat(testAgent.getAgentType()).isEqualTo(DEFAULT_AGENT_TYPE);
    }

    @Test
    void createAgentWithExistingId() throws Exception {
        // Create the Agent with an existing ID
        agent.setId(1L);
        AgentDTO agentDTO = agentMapper.toDto(agent);

        int databaseSizeBeforeCreate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        // set the field null
        agent.setName(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkContactNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        // set the field null
        agent.setContactNumber(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAgentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        // set the field null
        agent.setAgentType(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllAgents() {
        // Initialize the database
        agentRepository.save(agent).block();

        // Get all the agentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(agent.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].avatarUrl")
            .value(hasItem(DEFAULT_AVATAR_URL))
            .jsonPath("$.[*].agentType")
            .value(hasItem(DEFAULT_AGENT_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgentsWithEagerRelationshipsIsEnabled() {
        when(agentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(agentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgentsWithEagerRelationshipsIsNotEnabled() {
        when(agentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(agentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAgent() {
        // Initialize the database
        agentRepository.save(agent).block();

        // Get the agent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, agent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(agent.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.contactNumber")
            .value(is(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.avatarUrl")
            .value(is(DEFAULT_AVATAR_URL))
            .jsonPath("$.agentType")
            .value(is(DEFAULT_AGENT_TYPE.toString()));
    }

    @Test
    void getNonExistingAgent() {
        // Get the agent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAgent() throws Exception {
        // Initialize the database
        agentRepository.save(agent).block();

        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        agentSearchRepository.save(agent).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());

        // Update the agent
        Agent updatedAgent = agentRepository.findById(agent.getId()).block();
        updatedAgent.name(UPDATED_NAME).contactNumber(UPDATED_CONTACT_NUMBER).avatarUrl(UPDATED_AVATAR_URL).agentType(UPDATED_AGENT_TYPE);
        AgentDTO agentDTO = agentMapper.toDto(updatedAgent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, agentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        Agent testAgent = agentList.get(agentList.size() - 1);
        assertThat(testAgent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgent.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testAgent.getAvatarUrl()).isEqualTo(UPDATED_AVATAR_URL);
        assertThat(testAgent.getAgentType()).isEqualTo(UPDATED_AGENT_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Agent> agentSearchList = IterableUtils.toList(agentSearchRepository.findAll().collectList().block());
                Agent testAgentSearch = agentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAgentSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testAgentSearch.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
                assertThat(testAgentSearch.getAvatarUrl()).isEqualTo(UPDATED_AVATAR_URL);
                assertThat(testAgentSearch.getAgentType()).isEqualTo(UPDATED_AGENT_TYPE);
            });
    }

    @Test
    void putNonExistingAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, agentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        agentRepository.save(agent).block();

        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent.name(UPDATED_NAME).agentType(UPDATED_AGENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAgent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        Agent testAgent = agentList.get(agentList.size() - 1);
        assertThat(testAgent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgent.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testAgent.getAvatarUrl()).isEqualTo(DEFAULT_AVATAR_URL);
        assertThat(testAgent.getAgentType()).isEqualTo(UPDATED_AGENT_TYPE);
    }

    @Test
    void fullUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        agentRepository.save(agent).block();

        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent
            .name(UPDATED_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .avatarUrl(UPDATED_AVATAR_URL)
            .agentType(UPDATED_AGENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAgent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        Agent testAgent = agentList.get(agentList.size() - 1);
        assertThat(testAgent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgent.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testAgent.getAvatarUrl()).isEqualTo(UPDATED_AVATAR_URL);
        assertThat(testAgent.getAgentType()).isEqualTo(UPDATED_AGENT_TYPE);
    }

    @Test
    void patchNonExistingAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, agentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamAgent() throws Exception {
        int databaseSizeBeforeUpdate = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        agent.setId(count.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(agentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Agent in the database
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteAgent() {
        // Initialize the database
        agentRepository.save(agent).block();
        agentRepository.save(agent).block();
        agentSearchRepository.save(agent).block();

        int databaseSizeBeforeDelete = agentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the agent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, agent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Agent> agentList = agentRepository.findAll().collectList().block();
        assertThat(agentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchAgent() {
        // Initialize the database
        agent = agentRepository.save(agent).block();
        agentSearchRepository.save(agent).block();

        // Search the agent
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + agent.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(agent.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].avatarUrl")
            .value(hasItem(DEFAULT_AVATAR_URL))
            .jsonPath("$.[*].agentType")
            .value(hasItem(DEFAULT_AGENT_TYPE.toString()));
    }
}
