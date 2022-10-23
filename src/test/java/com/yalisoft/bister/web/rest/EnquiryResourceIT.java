package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.enumeration.EnquiryResolutionStatus;
import com.yalisoft.bister.domain.enumeration.EnquiryType;
import com.yalisoft.bister.repository.EnquiryRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.EnquirySearchRepository;
import com.yalisoft.bister.service.EnquiryService;
import com.yalisoft.bister.service.dto.EnquiryDTO;
import com.yalisoft.bister.service.mapper.EnquiryMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link EnquiryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnquiryResourceIT {

    private static final Instant DEFAULT_RAISED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RAISED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_RESPONSE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_RESPONSE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_LAST_RESPONSE_ID = 1L;
    private static final Long UPDATED_LAST_RESPONSE_ID = 2L;

    private static final EnquiryType DEFAULT_ENQUIRY_TYPE = EnquiryType.PROJECT;
    private static final EnquiryType UPDATED_ENQUIRY_TYPE = EnquiryType.PRODUCT;

    private static final EnquiryResolutionStatus DEFAULT_STATUS = EnquiryResolutionStatus.OPEN;
    private static final EnquiryResolutionStatus UPDATED_STATUS = EnquiryResolutionStatus.INPROGRESS;

    private static final String ENTITY_API_URL = "/api/enquiries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/enquiries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Mock
    private EnquiryRepository enquiryRepositoryMock;

    @Autowired
    private EnquiryMapper enquiryMapper;

    @Mock
    private EnquiryService enquiryServiceMock;

    @Autowired
    private EnquirySearchRepository enquirySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Enquiry enquiry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enquiry createEntity(EntityManager em) {
        Enquiry enquiry = new Enquiry()
            .raisedDate(DEFAULT_RAISED_DATE)
            .subject(DEFAULT_SUBJECT)
            .details(DEFAULT_DETAILS)
            .lastResponseDate(DEFAULT_LAST_RESPONSE_DATE)
            .lastResponseId(DEFAULT_LAST_RESPONSE_ID)
            .enquiryType(DEFAULT_ENQUIRY_TYPE)
            .status(DEFAULT_STATUS);
        return enquiry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enquiry createUpdatedEntity(EntityManager em) {
        Enquiry enquiry = new Enquiry()
            .raisedDate(UPDATED_RAISED_DATE)
            .subject(UPDATED_SUBJECT)
            .details(UPDATED_DETAILS)
            .lastResponseDate(UPDATED_LAST_RESPONSE_DATE)
            .lastResponseId(UPDATED_LAST_RESPONSE_ID)
            .enquiryType(UPDATED_ENQUIRY_TYPE)
            .status(UPDATED_STATUS);
        return enquiry;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Enquiry.class).block();
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
        enquirySearchRepository.deleteAll().block();
        assertThat(enquirySearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        enquiry = createEntity(em);
    }

    @Test
    void createEnquiry() throws Exception {
        int databaseSizeBeforeCreate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Enquiry testEnquiry = enquiryList.get(enquiryList.size() - 1);
        assertThat(testEnquiry.getRaisedDate()).isEqualTo(DEFAULT_RAISED_DATE);
        assertThat(testEnquiry.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testEnquiry.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testEnquiry.getLastResponseDate()).isEqualTo(DEFAULT_LAST_RESPONSE_DATE);
        assertThat(testEnquiry.getLastResponseId()).isEqualTo(DEFAULT_LAST_RESPONSE_ID);
        assertThat(testEnquiry.getEnquiryType()).isEqualTo(DEFAULT_ENQUIRY_TYPE);
        assertThat(testEnquiry.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createEnquiryWithExistingId() throws Exception {
        // Create the Enquiry with an existing ID
        enquiry.setId(1L);
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        int databaseSizeBeforeCreate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRaisedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        // set the field null
        enquiry.setRaisedDate(null);

        // Create the Enquiry, which fails.
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        // set the field null
        enquiry.setSubject(null);

        // Create the Enquiry, which fails.
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEnquiryTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        // set the field null
        enquiry.setEnquiryType(null);

        // Create the Enquiry, which fails.
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEnquiries() {
        // Initialize the database
        enquiryRepository.save(enquiry).block();

        // Get all the enquiryList
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
            .value(hasItem(enquiry.getId().intValue()))
            .jsonPath("$.[*].raisedDate")
            .value(hasItem(DEFAULT_RAISED_DATE.toString()))
            .jsonPath("$.[*].subject")
            .value(hasItem(DEFAULT_SUBJECT))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].lastResponseDate")
            .value(hasItem(DEFAULT_LAST_RESPONSE_DATE.toString()))
            .jsonPath("$.[*].lastResponseId")
            .value(hasItem(DEFAULT_LAST_RESPONSE_ID.intValue()))
            .jsonPath("$.[*].enquiryType")
            .value(hasItem(DEFAULT_ENQUIRY_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnquiriesWithEagerRelationshipsIsEnabled() {
        when(enquiryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(enquiryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnquiriesWithEagerRelationshipsIsNotEnabled() {
        when(enquiryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(enquiryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEnquiry() {
        // Initialize the database
        enquiryRepository.save(enquiry).block();

        // Get the enquiry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, enquiry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(enquiry.getId().intValue()))
            .jsonPath("$.raisedDate")
            .value(is(DEFAULT_RAISED_DATE.toString()))
            .jsonPath("$.subject")
            .value(is(DEFAULT_SUBJECT))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.lastResponseDate")
            .value(is(DEFAULT_LAST_RESPONSE_DATE.toString()))
            .jsonPath("$.lastResponseId")
            .value(is(DEFAULT_LAST_RESPONSE_ID.intValue()))
            .jsonPath("$.enquiryType")
            .value(is(DEFAULT_ENQUIRY_TYPE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingEnquiry() {
        // Get the enquiry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEnquiry() throws Exception {
        // Initialize the database
        enquiryRepository.save(enquiry).block();

        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        enquirySearchRepository.save(enquiry).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());

        // Update the enquiry
        Enquiry updatedEnquiry = enquiryRepository.findById(enquiry.getId()).block();
        updatedEnquiry
            .raisedDate(UPDATED_RAISED_DATE)
            .subject(UPDATED_SUBJECT)
            .details(UPDATED_DETAILS)
            .lastResponseDate(UPDATED_LAST_RESPONSE_DATE)
            .lastResponseId(UPDATED_LAST_RESPONSE_ID)
            .enquiryType(UPDATED_ENQUIRY_TYPE)
            .status(UPDATED_STATUS);
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(updatedEnquiry);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, enquiryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        Enquiry testEnquiry = enquiryList.get(enquiryList.size() - 1);
        assertThat(testEnquiry.getRaisedDate()).isEqualTo(UPDATED_RAISED_DATE);
        assertThat(testEnquiry.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEnquiry.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testEnquiry.getLastResponseDate()).isEqualTo(UPDATED_LAST_RESPONSE_DATE);
        assertThat(testEnquiry.getLastResponseId()).isEqualTo(UPDATED_LAST_RESPONSE_ID);
        assertThat(testEnquiry.getEnquiryType()).isEqualTo(UPDATED_ENQUIRY_TYPE);
        assertThat(testEnquiry.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Enquiry> enquirySearchList = IterableUtils.toList(enquirySearchRepository.findAll().collectList().block());
                Enquiry testEnquirySearch = enquirySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEnquirySearch.getRaisedDate()).isEqualTo(UPDATED_RAISED_DATE);
                assertThat(testEnquirySearch.getSubject()).isEqualTo(UPDATED_SUBJECT);
                assertThat(testEnquirySearch.getDetails()).isEqualTo(UPDATED_DETAILS);
                assertThat(testEnquirySearch.getLastResponseDate()).isEqualTo(UPDATED_LAST_RESPONSE_DATE);
                assertThat(testEnquirySearch.getLastResponseId()).isEqualTo(UPDATED_LAST_RESPONSE_ID);
                assertThat(testEnquirySearch.getEnquiryType()).isEqualTo(UPDATED_ENQUIRY_TYPE);
                assertThat(testEnquirySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    void putNonExistingEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, enquiryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEnquiryWithPatch() throws Exception {
        // Initialize the database
        enquiryRepository.save(enquiry).block();

        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();

        // Update the enquiry using partial update
        Enquiry partialUpdatedEnquiry = new Enquiry();
        partialUpdatedEnquiry.setId(enquiry.getId());

        partialUpdatedEnquiry
            .subject(UPDATED_SUBJECT)
            .lastResponseDate(UPDATED_LAST_RESPONSE_DATE)
            .enquiryType(UPDATED_ENQUIRY_TYPE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnquiry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEnquiry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        Enquiry testEnquiry = enquiryList.get(enquiryList.size() - 1);
        assertThat(testEnquiry.getRaisedDate()).isEqualTo(DEFAULT_RAISED_DATE);
        assertThat(testEnquiry.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEnquiry.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testEnquiry.getLastResponseDate()).isEqualTo(UPDATED_LAST_RESPONSE_DATE);
        assertThat(testEnquiry.getLastResponseId()).isEqualTo(DEFAULT_LAST_RESPONSE_ID);
        assertThat(testEnquiry.getEnquiryType()).isEqualTo(UPDATED_ENQUIRY_TYPE);
        assertThat(testEnquiry.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateEnquiryWithPatch() throws Exception {
        // Initialize the database
        enquiryRepository.save(enquiry).block();

        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();

        // Update the enquiry using partial update
        Enquiry partialUpdatedEnquiry = new Enquiry();
        partialUpdatedEnquiry.setId(enquiry.getId());

        partialUpdatedEnquiry
            .raisedDate(UPDATED_RAISED_DATE)
            .subject(UPDATED_SUBJECT)
            .details(UPDATED_DETAILS)
            .lastResponseDate(UPDATED_LAST_RESPONSE_DATE)
            .lastResponseId(UPDATED_LAST_RESPONSE_ID)
            .enquiryType(UPDATED_ENQUIRY_TYPE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnquiry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEnquiry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        Enquiry testEnquiry = enquiryList.get(enquiryList.size() - 1);
        assertThat(testEnquiry.getRaisedDate()).isEqualTo(UPDATED_RAISED_DATE);
        assertThat(testEnquiry.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEnquiry.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testEnquiry.getLastResponseDate()).isEqualTo(UPDATED_LAST_RESPONSE_DATE);
        assertThat(testEnquiry.getLastResponseId()).isEqualTo(UPDATED_LAST_RESPONSE_ID);
        assertThat(testEnquiry.getEnquiryType()).isEqualTo(UPDATED_ENQUIRY_TYPE);
        assertThat(testEnquiry.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, enquiryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEnquiry() throws Exception {
        int databaseSizeBeforeUpdate = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        enquiry.setId(count.incrementAndGet());

        // Create the Enquiry
        EnquiryDTO enquiryDTO = enquiryMapper.toDto(enquiry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(enquiryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Enquiry in the database
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEnquiry() {
        // Initialize the database
        enquiryRepository.save(enquiry).block();
        enquiryRepository.save(enquiry).block();
        enquirySearchRepository.save(enquiry).block();

        int databaseSizeBeforeDelete = enquiryRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the enquiry
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, enquiry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Enquiry> enquiryList = enquiryRepository.findAll().collectList().block();
        assertThat(enquiryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(enquirySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEnquiry() {
        // Initialize the database
        enquiry = enquiryRepository.save(enquiry).block();
        enquirySearchRepository.save(enquiry).block();

        // Search the enquiry
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + enquiry.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(enquiry.getId().intValue()))
            .jsonPath("$.[*].raisedDate")
            .value(hasItem(DEFAULT_RAISED_DATE.toString()))
            .jsonPath("$.[*].subject")
            .value(hasItem(DEFAULT_SUBJECT))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].lastResponseDate")
            .value(hasItem(DEFAULT_LAST_RESPONSE_DATE.toString()))
            .jsonPath("$.[*].lastResponseId")
            .value(hasItem(DEFAULT_LAST_RESPONSE_ID.intValue()))
            .jsonPath("$.[*].enquiryType")
            .value(hasItem(DEFAULT_ENQUIRY_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }
}
