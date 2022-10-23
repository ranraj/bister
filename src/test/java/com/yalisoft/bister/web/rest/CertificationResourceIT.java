package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Certification;
import com.yalisoft.bister.domain.enumeration.CertificationStatus;
import com.yalisoft.bister.repository.CertificationRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.CertificationSearchRepository;
import com.yalisoft.bister.service.dto.CertificationDTO;
import com.yalisoft.bister.service.mapper.CertificationMapper;
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
 * Integration tests for the {@link CertificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CertificationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHORITY = "AAAAAAAAAA";
    private static final String UPDATED_AUTHORITY = "BBBBBBBBBB";

    private static final CertificationStatus DEFAULT_STATUS = CertificationStatus.PLANNED;
    private static final CertificationStatus UPDATED_STATUS = CertificationStatus.PREPARATION;

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final Long DEFAULT_PRODCUT = 1L;
    private static final Long UPDATED_PRODCUT = 2L;

    private static final Long DEFAULT_ORG_ID = 1L;
    private static final Long UPDATED_ORG_ID = 2L;

    private static final Long DEFAULT_FACITLITY_ID = 1L;
    private static final Long UPDATED_FACITLITY_ID = 2L;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/certifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/certifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private CertificationMapper certificationMapper;

    @Autowired
    private CertificationSearchRepository certificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Certification certification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certification createEntity(EntityManager em) {
        Certification certification = new Certification()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .authority(DEFAULT_AUTHORITY)
            .status(DEFAULT_STATUS)
            .projectId(DEFAULT_PROJECT_ID)
            .prodcut(DEFAULT_PRODCUT)
            .orgId(DEFAULT_ORG_ID)
            .facitlityId(DEFAULT_FACITLITY_ID)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT);
        return certification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certification createUpdatedEntity(EntityManager em) {
        Certification certification = new Certification()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .authority(UPDATED_AUTHORITY)
            .status(UPDATED_STATUS)
            .projectId(UPDATED_PROJECT_ID)
            .prodcut(UPDATED_PRODCUT)
            .orgId(UPDATED_ORG_ID)
            .facitlityId(UPDATED_FACITLITY_ID)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
        return certification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Certification.class).block();
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
        certificationSearchRepository.deleteAll().block();
        assertThat(certificationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        certification = createEntity(em);
    }

    @Test
    void createCertification() throws Exception {
        int databaseSizeBeforeCreate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Certification testCertification = certificationList.get(certificationList.size() - 1);
        assertThat(testCertification.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCertification.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testCertification.getAuthority()).isEqualTo(DEFAULT_AUTHORITY);
        assertThat(testCertification.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCertification.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testCertification.getProdcut()).isEqualTo(DEFAULT_PRODCUT);
        assertThat(testCertification.getOrgId()).isEqualTo(DEFAULT_ORG_ID);
        assertThat(testCertification.getFacitlityId()).isEqualTo(DEFAULT_FACITLITY_ID);
        assertThat(testCertification.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCertification.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    void createCertificationWithExistingId() throws Exception {
        // Create the Certification with an existing ID
        certification.setId(1L);
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        int databaseSizeBeforeCreate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // set the field null
        certification.setName(null);

        // Create the Certification, which fails.
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAuthorityIsRequired() throws Exception {
        int databaseSizeBeforeTest = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // set the field null
        certification.setAuthority(null);

        // Create the Certification, which fails.
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // set the field null
        certification.setStatus(null);

        // Create the Certification, which fails.
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // set the field null
        certification.setCreatedBy(null);

        // Create the Certification, which fails.
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        // set the field null
        certification.setCreatedAt(null);

        // Create the Certification, which fails.
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCertifications() {
        // Initialize the database
        certificationRepository.save(certification).block();

        // Get all the certificationList
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
            .value(hasItem(certification.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].authority")
            .value(hasItem(DEFAULT_AUTHORITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].prodcut")
            .value(hasItem(DEFAULT_PRODCUT.intValue()))
            .jsonPath("$.[*].orgId")
            .value(hasItem(DEFAULT_ORG_ID.intValue()))
            .jsonPath("$.[*].facitlityId")
            .value(hasItem(DEFAULT_FACITLITY_ID.intValue()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getCertification() {
        // Initialize the database
        certificationRepository.save(certification).block();

        // Get the certification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, certification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(certification.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.authority")
            .value(is(DEFAULT_AUTHORITY))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.projectId")
            .value(is(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.prodcut")
            .value(is(DEFAULT_PRODCUT.intValue()))
            .jsonPath("$.orgId")
            .value(is(DEFAULT_ORG_ID.intValue()))
            .jsonPath("$.facitlityId")
            .value(is(DEFAULT_FACITLITY_ID.intValue()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getNonExistingCertification() {
        // Get the certification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCertification() throws Exception {
        // Initialize the database
        certificationRepository.save(certification).block();

        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        certificationSearchRepository.save(certification).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());

        // Update the certification
        Certification updatedCertification = certificationRepository.findById(certification.getId()).block();
        updatedCertification
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .authority(UPDATED_AUTHORITY)
            .status(UPDATED_STATUS)
            .projectId(UPDATED_PROJECT_ID)
            .prodcut(UPDATED_PRODCUT)
            .orgId(UPDATED_ORG_ID)
            .facitlityId(UPDATED_FACITLITY_ID)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);
        CertificationDTO certificationDTO = certificationMapper.toDto(updatedCertification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, certificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        Certification testCertification = certificationList.get(certificationList.size() - 1);
        assertThat(testCertification.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertification.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testCertification.getAuthority()).isEqualTo(UPDATED_AUTHORITY);
        assertThat(testCertification.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCertification.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testCertification.getProdcut()).isEqualTo(UPDATED_PRODCUT);
        assertThat(testCertification.getOrgId()).isEqualTo(UPDATED_ORG_ID);
        assertThat(testCertification.getFacitlityId()).isEqualTo(UPDATED_FACITLITY_ID);
        assertThat(testCertification.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCertification.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Certification> certificationSearchList = IterableUtils.toList(
                    certificationSearchRepository.findAll().collectList().block()
                );
                Certification testCertificationSearch = certificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCertificationSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testCertificationSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testCertificationSearch.getAuthority()).isEqualTo(UPDATED_AUTHORITY);
                assertThat(testCertificationSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testCertificationSearch.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
                assertThat(testCertificationSearch.getProdcut()).isEqualTo(UPDATED_PRODCUT);
                assertThat(testCertificationSearch.getOrgId()).isEqualTo(UPDATED_ORG_ID);
                assertThat(testCertificationSearch.getFacitlityId()).isEqualTo(UPDATED_FACITLITY_ID);
                assertThat(testCertificationSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
                assertThat(testCertificationSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
            });
    }

    @Test
    void putNonExistingCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, certificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCertificationWithPatch() throws Exception {
        // Initialize the database
        certificationRepository.save(certification).block();

        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();

        // Update the certification using partial update
        Certification partialUpdatedCertification = new Certification();
        partialUpdatedCertification.setId(certification.getId());

        partialUpdatedCertification.name(UPDATED_NAME).prodcut(UPDATED_PRODCUT).facitlityId(UPDATED_FACITLITY_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCertification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCertification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        Certification testCertification = certificationList.get(certificationList.size() - 1);
        assertThat(testCertification.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertification.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testCertification.getAuthority()).isEqualTo(DEFAULT_AUTHORITY);
        assertThat(testCertification.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCertification.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testCertification.getProdcut()).isEqualTo(UPDATED_PRODCUT);
        assertThat(testCertification.getOrgId()).isEqualTo(DEFAULT_ORG_ID);
        assertThat(testCertification.getFacitlityId()).isEqualTo(UPDATED_FACITLITY_ID);
        assertThat(testCertification.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCertification.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    void fullUpdateCertificationWithPatch() throws Exception {
        // Initialize the database
        certificationRepository.save(certification).block();

        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();

        // Update the certification using partial update
        Certification partialUpdatedCertification = new Certification();
        partialUpdatedCertification.setId(certification.getId());

        partialUpdatedCertification
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .authority(UPDATED_AUTHORITY)
            .status(UPDATED_STATUS)
            .projectId(UPDATED_PROJECT_ID)
            .prodcut(UPDATED_PRODCUT)
            .orgId(UPDATED_ORG_ID)
            .facitlityId(UPDATED_FACITLITY_ID)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCertification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCertification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        Certification testCertification = certificationList.get(certificationList.size() - 1);
        assertThat(testCertification.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertification.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testCertification.getAuthority()).isEqualTo(UPDATED_AUTHORITY);
        assertThat(testCertification.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCertification.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testCertification.getProdcut()).isEqualTo(UPDATED_PRODCUT);
        assertThat(testCertification.getOrgId()).isEqualTo(UPDATED_ORG_ID);
        assertThat(testCertification.getFacitlityId()).isEqualTo(UPDATED_FACITLITY_ID);
        assertThat(testCertification.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCertification.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    void patchNonExistingCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, certificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCertification() throws Exception {
        int databaseSizeBeforeUpdate = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        certification.setId(count.incrementAndGet());

        // Create the Certification
        CertificationDTO certificationDTO = certificationMapper.toDto(certification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(certificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Certification in the database
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCertification() {
        // Initialize the database
        certificationRepository.save(certification).block();
        certificationRepository.save(certification).block();
        certificationSearchRepository.save(certification).block();

        int databaseSizeBeforeDelete = certificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the certification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, certification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Certification> certificationList = certificationRepository.findAll().collectList().block();
        assertThat(certificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(certificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCertification() {
        // Initialize the database
        certification = certificationRepository.save(certification).block();
        certificationSearchRepository.save(certification).block();

        // Search the certification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + certification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(certification.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].authority")
            .value(hasItem(DEFAULT_AUTHORITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].prodcut")
            .value(hasItem(DEFAULT_PRODCUT.intValue()))
            .jsonPath("$.[*].orgId")
            .value(hasItem(DEFAULT_ORG_ID.intValue()))
            .jsonPath("$.[*].facitlityId")
            .value(hasItem(DEFAULT_FACITLITY_ID.intValue()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }
}
