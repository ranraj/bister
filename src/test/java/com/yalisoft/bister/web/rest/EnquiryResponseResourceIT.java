package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.EnquiryResponse;
import com.yalisoft.bister.domain.enumeration.EnquiryResponseType;
import com.yalisoft.bister.repository.EnquiryResponseRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.EnquiryResponseSearchRepository;
import com.yalisoft.bister.service.EnquiryResponseService;
import com.yalisoft.bister.service.dto.EnquiryResponseDTO;
import com.yalisoft.bister.service.mapper.EnquiryResponseMapper;
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
 * Integration tests for the {@link EnquiryResponseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnquiryResponseResourceIT {

    private static final String DEFAULT_QUERY = "AAAAAAAAAA";
    private static final String UPDATED_QUERY = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final EnquiryResponseType DEFAULT_ENQUIRY_RESPONSE_TYPE = EnquiryResponseType.INPERSON;
    private static final EnquiryResponseType UPDATED_ENQUIRY_RESPONSE_TYPE = EnquiryResponseType.CALL;

    private static final String ENTITY_API_URL = "/api/enquiry-responses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/enquiry-responses";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnquiryResponseRepository enquiryResponseRepository;

    @Mock
    private EnquiryResponseRepository enquiryResponseRepositoryMock;

    @Autowired
    private EnquiryResponseMapper enquiryResponseMapper;

    @Mock
    private EnquiryResponseService enquiryResponseServiceMock;

    @Autowired
    private EnquiryResponseSearchRepository enquiryResponseSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EnquiryResponse enquiryResponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnquiryResponse createEntity(EntityManager em) {
        EnquiryResponse enquiryResponse = new EnquiryResponse()
            .query(DEFAULT_QUERY)
            .details(DEFAULT_DETAILS)
            .enquiryResponseType(DEFAULT_ENQUIRY_RESPONSE_TYPE);
        return enquiryResponse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnquiryResponse createUpdatedEntity(EntityManager em) {
        EnquiryResponse enquiryResponse = new EnquiryResponse()
            .query(UPDATED_QUERY)
            .details(UPDATED_DETAILS)
            .enquiryResponseType(UPDATED_ENQUIRY_RESPONSE_TYPE);
        return enquiryResponse;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EnquiryResponse.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        enquiryResponseSearchRepository.deleteAll().block();
        assertThat(enquiryResponseSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        enquiryResponse = createEntity(em);
    }

    @Test
    void createEnquiryResponse() throws Exception {
        int databaseSizeBeforeCreate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        EnquiryResponse testEnquiryResponse = enquiryResponseList.get(enquiryResponseList.size() - 1);
        assertThat(testEnquiryResponse.getQuery()).isEqualTo(DEFAULT_QUERY);
        assertThat(testEnquiryResponse.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testEnquiryResponse.getEnquiryResponseType()).isEqualTo(DEFAULT_ENQUIRY_RESPONSE_TYPE);
    }

    @Test
    void createEnquiryResponseWithExistingId() throws Exception {
        // Create the EnquiryResponse with an existing ID
        enquiryResponse.setId(1L);
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        int databaseSizeBeforeCreate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEnquiryResponseTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        // set the field null
        enquiryResponse.setEnquiryResponseType(null);

        // Create the EnquiryResponse, which fails.
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEnquiryResponses() {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();

        // Get all the enquiryResponseList
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
            .value(hasItem(enquiryResponse.getId().intValue()))
            .jsonPath("$.[*].query")
            .value(hasItem(DEFAULT_QUERY))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].enquiryResponseType")
            .value(hasItem(DEFAULT_ENQUIRY_RESPONSE_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnquiryResponsesWithEagerRelationshipsIsEnabled() {
        when(enquiryResponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(enquiryResponseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnquiryResponsesWithEagerRelationshipsIsNotEnabled() {
        when(enquiryResponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(enquiryResponseRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEnquiryResponse() {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();

        // Get the enquiryResponse
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, enquiryResponse.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(enquiryResponse.getId().intValue()))
            .jsonPath("$.query")
            .value(is(DEFAULT_QUERY))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.enquiryResponseType")
            .value(is(DEFAULT_ENQUIRY_RESPONSE_TYPE.toString()));
    }

    @Test
    void getNonExistingEnquiryResponse() {
        // Get the enquiryResponse
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEnquiryResponse() throws Exception {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();

        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        enquiryResponseSearchRepository.save(enquiryResponse).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());

        // Update the enquiryResponse
        EnquiryResponse updatedEnquiryResponse = enquiryResponseRepository.findById(enquiryResponse.getId()).block();
        updatedEnquiryResponse.query(UPDATED_QUERY).details(UPDATED_DETAILS).enquiryResponseType(UPDATED_ENQUIRY_RESPONSE_TYPE);
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(updatedEnquiryResponse);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, enquiryResponseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        EnquiryResponse testEnquiryResponse = enquiryResponseList.get(enquiryResponseList.size() - 1);
        assertThat(testEnquiryResponse.getQuery()).isEqualTo(UPDATED_QUERY);
        assertThat(testEnquiryResponse.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testEnquiryResponse.getEnquiryResponseType()).isEqualTo(UPDATED_ENQUIRY_RESPONSE_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EnquiryResponse> enquiryResponseSearchList = IterableUtils.toList(
                    enquiryResponseSearchRepository.findAll().collectList().block()
                );
                EnquiryResponse testEnquiryResponseSearch = enquiryResponseSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEnquiryResponseSearch.getQuery()).isEqualTo(UPDATED_QUERY);
                assertThat(testEnquiryResponseSearch.getDetails()).isEqualTo(UPDATED_DETAILS);
                assertThat(testEnquiryResponseSearch.getEnquiryResponseType()).isEqualTo(UPDATED_ENQUIRY_RESPONSE_TYPE);
            });
    }

    @Test
    void putNonExistingEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, enquiryResponseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEnquiryResponseWithPatch() throws Exception {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();

        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();

        // Update the enquiryResponse using partial update
        EnquiryResponse partialUpdatedEnquiryResponse = new EnquiryResponse();
        partialUpdatedEnquiryResponse.setId(enquiryResponse.getId());

        partialUpdatedEnquiryResponse.query(UPDATED_QUERY).enquiryResponseType(UPDATED_ENQUIRY_RESPONSE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnquiryResponse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEnquiryResponse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        EnquiryResponse testEnquiryResponse = enquiryResponseList.get(enquiryResponseList.size() - 1);
        assertThat(testEnquiryResponse.getQuery()).isEqualTo(UPDATED_QUERY);
        assertThat(testEnquiryResponse.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testEnquiryResponse.getEnquiryResponseType()).isEqualTo(UPDATED_ENQUIRY_RESPONSE_TYPE);
    }

    @Test
    void fullUpdateEnquiryResponseWithPatch() throws Exception {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();

        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();

        // Update the enquiryResponse using partial update
        EnquiryResponse partialUpdatedEnquiryResponse = new EnquiryResponse();
        partialUpdatedEnquiryResponse.setId(enquiryResponse.getId());

        partialUpdatedEnquiryResponse.query(UPDATED_QUERY).details(UPDATED_DETAILS).enquiryResponseType(UPDATED_ENQUIRY_RESPONSE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnquiryResponse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEnquiryResponse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        EnquiryResponse testEnquiryResponse = enquiryResponseList.get(enquiryResponseList.size() - 1);
        assertThat(testEnquiryResponse.getQuery()).isEqualTo(UPDATED_QUERY);
        assertThat(testEnquiryResponse.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testEnquiryResponse.getEnquiryResponseType()).isEqualTo(UPDATED_ENQUIRY_RESPONSE_TYPE);
    }

    @Test
    void patchNonExistingEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, enquiryResponseDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEnquiryResponse() throws Exception {
        int databaseSizeBeforeUpdate = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        enquiryResponse.setId(count.incrementAndGet());

        // Create the EnquiryResponse
        EnquiryResponseDTO enquiryResponseDTO = enquiryResponseMapper.toDto(enquiryResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryResponseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnquiryResponse in the database
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEnquiryResponse() {
        // Initialize the database
        enquiryResponseRepository.save(enquiryResponse).block();
        enquiryResponseRepository.save(enquiryResponse).block();
        enquiryResponseSearchRepository.save(enquiryResponse).block();

        int databaseSizeBeforeDelete = enquiryResponseRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the enquiryResponse
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, enquiryResponse.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<EnquiryResponse> enquiryResponseList = enquiryResponseRepository.findAll().collectList().block();
        assertThat(enquiryResponseList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquiryResponseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEnquiryResponse() {
        // Initialize the database
        enquiryResponse = enquiryResponseRepository.save(enquiryResponse).block();
        enquiryResponseSearchRepository.save(enquiryResponse).block();

        // Search the enquiryResponse
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + enquiryResponse.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(enquiryResponse.getId().intValue()))
            .jsonPath("$.[*].query")
            .value(hasItem(DEFAULT_QUERY))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].enquiryResponseType")
            .value(hasItem(DEFAULT_ENQUIRY_RESPONSE_TYPE.toString()));
    }
}
