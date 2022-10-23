package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.OrganisationRepository;
import com.yalisoft.bister.repository.search.OrganisationSearchRepository;
import com.yalisoft.bister.service.OrganisationService;
import com.yalisoft.bister.service.dto.OrganisationDTO;
import com.yalisoft.bister.service.mapper.OrganisationMapper;
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
 * Integration tests for the {@link OrganisationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganisationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/organisations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganisationRepository organisationRepository;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Mock
    private OrganisationService organisationServiceMock;

    @Autowired
    private OrganisationSearchRepository organisationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Organisation organisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createEntity(EntityManager em) {
        Organisation organisation = new Organisation().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).key(DEFAULT_KEY);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createEntity(em)).block();
        organisation.setAddress(address);
        return organisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createUpdatedEntity(EntityManager em) {
        Organisation organisation = new Organisation().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createUpdatedEntity(em)).block();
        organisation.setAddress(address);
        return organisation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Organisation.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        AddressResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        organisationSearchRepository.deleteAll().block();
        assertThat(organisationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        organisation = createEntity(em);
    }

    @Test
    void createOrganisation() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganisation.getKey()).isEqualTo(DEFAULT_KEY);
    }

    @Test
    void createOrganisationWithExistingId() throws Exception {
        // Create the Organisation with an existing ID
        organisation.setId(1L);
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        int databaseSizeBeforeCreate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        // set the field null
        organisation.setName(null);

        // Create the Organisation, which fails.
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        // set the field null
        organisation.setDescription(null);

        // Create the Organisation, which fails.
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        // set the field null
        organisation.setKey(null);

        // Create the Organisation, which fails.
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllOrganisations() {
        // Initialize the database
        organisationRepository.save(organisation).block();

        // Get all the organisationList
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
            .value(hasItem(organisation.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].key")
            .value(hasItem(DEFAULT_KEY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganisationsWithEagerRelationshipsIsEnabled() {
        when(organisationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(organisationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganisationsWithEagerRelationshipsIsNotEnabled() {
        when(organisationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(organisationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOrganisation() {
        // Initialize the database
        organisationRepository.save(organisation).block();

        // Get the organisation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organisation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organisation.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.key")
            .value(is(DEFAULT_KEY));
    }

    @Test
    void getNonExistingOrganisation() {
        // Get the organisation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        organisationSearchRepository.save(organisation).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());

        // Update the organisation
        Organisation updatedOrganisation = organisationRepository.findById(organisation.getId()).block();
        updatedOrganisation.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);
        OrganisationDTO organisationDTO = organisationMapper.toDto(updatedOrganisation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganisation.getKey()).isEqualTo(UPDATED_KEY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Organisation> organisationSearchList = IterableUtils.toList(
                    organisationSearchRepository.findAll().collectList().block()
                );
                Organisation testOrganisationSearch = organisationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrganisationSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testOrganisationSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testOrganisationSearch.getKey()).isEqualTo(UPDATED_KEY);
            });
    }

    @Test
    void putNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.description(UPDATED_DESCRIPTION).key(UPDATED_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganisation.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    void fullUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.save(organisation).block();

        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganisation.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    void patchNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organisationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        organisation.setId(count.incrementAndGet());

        // Create the Organisation
        OrganisationDTO organisationDTO = organisationMapper.toDto(organisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organisationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteOrganisation() {
        // Initialize the database
        organisationRepository.save(organisation).block();
        organisationRepository.save(organisation).block();
        organisationSearchRepository.save(organisation).block();

        int databaseSizeBeforeDelete = organisationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the organisation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organisation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Organisation> organisationList = organisationRepository.findAll().collectList().block();
        assertThat(organisationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organisationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchOrganisation() {
        // Initialize the database
        organisation = organisationRepository.save(organisation).block();
        organisationSearchRepository.save(organisation).block();

        // Search the organisation
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + organisation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(organisation.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].key")
            .value(hasItem(DEFAULT_KEY));
    }
}
