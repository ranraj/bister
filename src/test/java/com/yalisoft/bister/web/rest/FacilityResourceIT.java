package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.domain.enumeration.FacilityType;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.FacilityRepository;
import com.yalisoft.bister.repository.search.FacilitySearchRepository;
import com.yalisoft.bister.service.FacilityService;
import com.yalisoft.bister.service.dto.FacilityDTO;
import com.yalisoft.bister.service.mapper.FacilityMapper;
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
 * Integration tests for the {@link FacilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FacilityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final FacilityType DEFAULT_FACILITY_TYPE = FacilityType.FRONT_OFFICE;
    private static final FacilityType UPDATED_FACILITY_TYPE = FacilityType.BACK_OFFICE;

    private static final String ENTITY_API_URL = "/api/facilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/facilities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacilityRepository facilityRepository;

    @Mock
    private FacilityRepository facilityRepositoryMock;

    @Autowired
    private FacilityMapper facilityMapper;

    @Mock
    private FacilityService facilityServiceMock;

    @Autowired
    private FacilitySearchRepository facilitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Facility facility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createEntity(EntityManager em) {
        Facility facility = new Facility().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).facilityType(DEFAULT_FACILITY_TYPE);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createEntity(em)).block();
        facility.setAddress(address);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        facility.setUser(user);
        // Add required entity
        Organisation organisation;
        organisation = em.insert(OrganisationResourceIT.createEntity(em)).block();
        facility.setOrganisation(organisation);
        return facility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createUpdatedEntity(EntityManager em) {
        Facility facility = new Facility().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).facilityType(UPDATED_FACILITY_TYPE);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createUpdatedEntity(em)).block();
        facility.setAddress(address);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        facility.setUser(user);
        // Add required entity
        Organisation organisation;
        organisation = em.insert(OrganisationResourceIT.createUpdatedEntity(em)).block();
        facility.setOrganisation(organisation);
        return facility;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Facility.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        AddressResourceIT.deleteEntities(em);
        UserResourceIT.deleteEntities(em);
        OrganisationResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        facilitySearchRepository.deleteAll().block();
        assertThat(facilitySearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        facility = createEntity(em);
    }

    @Test
    void createFacility() throws Exception {
        int databaseSizeBeforeCreate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFacility.getFacilityType()).isEqualTo(DEFAULT_FACILITY_TYPE);
    }

    @Test
    void createFacilityWithExistingId() throws Exception {
        // Create the Facility with an existing ID
        facility.setId(1L);
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        int databaseSizeBeforeCreate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        // set the field null
        facility.setName(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        // set the field null
        facility.setDescription(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFacilityTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        // set the field null
        facility.setFacilityType(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllFacilities() {
        // Initialize the database
        facilityRepository.save(facility).block();

        // Get all the facilityList
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
            .value(hasItem(facility.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].facilityType")
            .value(hasItem(DEFAULT_FACILITY_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFacilitiesWithEagerRelationshipsIsEnabled() {
        when(facilityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(facilityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFacilitiesWithEagerRelationshipsIsNotEnabled() {
        when(facilityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(facilityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getFacility() {
        // Initialize the database
        facilityRepository.save(facility).block();

        // Get the facility
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, facility.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(facility.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.facilityType")
            .value(is(DEFAULT_FACILITY_TYPE.toString()));
    }

    @Test
    void getNonExistingFacility() {
        // Get the facility
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFacility() throws Exception {
        // Initialize the database
        facilityRepository.save(facility).block();

        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        facilitySearchRepository.save(facility).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());

        // Update the facility
        Facility updatedFacility = facilityRepository.findById(facility.getId()).block();
        updatedFacility.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).facilityType(UPDATED_FACILITY_TYPE);
        FacilityDTO facilityDTO = facilityMapper.toDto(updatedFacility);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, facilityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getFacilityType()).isEqualTo(UPDATED_FACILITY_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Facility> facilitySearchList = IterableUtils.toList(facilitySearchRepository.findAll().collectList().block());
                Facility testFacilitySearch = facilitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFacilitySearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testFacilitySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testFacilitySearch.getFacilityType()).isEqualTo(UPDATED_FACILITY_TYPE);
            });
    }

    @Test
    void putNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, facilityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.save(facility).block();

        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        partialUpdatedFacility.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getFacilityType()).isEqualTo(DEFAULT_FACILITY_TYPE);
    }

    @Test
    void fullUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.save(facility).block();

        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        partialUpdatedFacility.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).facilityType(UPDATED_FACILITY_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getFacilityType()).isEqualTo(UPDATED_FACILITY_TYPE);
    }

    @Test
    void patchNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, facilityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        facility.setId(count.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(facilityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteFacility() {
        // Initialize the database
        facilityRepository.save(facility).block();
        facilityRepository.save(facility).block();
        facilitySearchRepository.save(facility).block();

        int databaseSizeBeforeDelete = facilityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the facility
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, facility.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Facility> facilityList = facilityRepository.findAll().collectList().block();
        assertThat(facilityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchFacility() {
        // Initialize the database
        facility = facilityRepository.save(facility).block();
        facilitySearchRepository.save(facility).block();

        // Search the facility
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + facility.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(facility.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].facilityType")
            .value(hasItem(DEFAULT_FACILITY_TYPE.toString()));
    }
}
