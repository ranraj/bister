package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Phonenumber;
import com.yalisoft.bister.domain.enumeration.PhonenumberType;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.PhonenumberRepository;
import com.yalisoft.bister.repository.search.PhonenumberSearchRepository;
import com.yalisoft.bister.service.PhonenumberService;
import com.yalisoft.bister.service.dto.PhonenumberDTO;
import com.yalisoft.bister.service.mapper.PhonenumberMapper;
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
 * Integration tests for the {@link PhonenumberResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PhonenumberResourceIT {

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final PhonenumberType DEFAULT_PHONENUMBER_TYPE = PhonenumberType.OFFICE_PRIMARY;
    private static final PhonenumberType UPDATED_PHONENUMBER_TYPE = PhonenumberType.OFFICE_SECONDARY;

    private static final String ENTITY_API_URL = "/api/phonenumbers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/phonenumbers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PhonenumberRepository phonenumberRepository;

    @Mock
    private PhonenumberRepository phonenumberRepositoryMock;

    @Autowired
    private PhonenumberMapper phonenumberMapper;

    @Mock
    private PhonenumberService phonenumberServiceMock;

    @Autowired
    private PhonenumberSearchRepository phonenumberSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Phonenumber phonenumber;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Phonenumber createEntity(EntityManager em) {
        Phonenumber phonenumber = new Phonenumber()
            .country(DEFAULT_COUNTRY)
            .code(DEFAULT_CODE)
            .contactNumber(DEFAULT_CONTACT_NUMBER)
            .phonenumberType(DEFAULT_PHONENUMBER_TYPE);
        return phonenumber;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Phonenumber createUpdatedEntity(EntityManager em) {
        Phonenumber phonenumber = new Phonenumber()
            .country(UPDATED_COUNTRY)
            .code(UPDATED_CODE)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .phonenumberType(UPDATED_PHONENUMBER_TYPE);
        return phonenumber;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Phonenumber.class).block();
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
        phonenumberSearchRepository.deleteAll().block();
        assertThat(phonenumberSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        phonenumber = createEntity(em);
    }

    @Test
    void createPhonenumber() throws Exception {
        int databaseSizeBeforeCreate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Phonenumber testPhonenumber = phonenumberList.get(phonenumberList.size() - 1);
        assertThat(testPhonenumber.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testPhonenumber.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPhonenumber.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testPhonenumber.getPhonenumberType()).isEqualTo(DEFAULT_PHONENUMBER_TYPE);
    }

    @Test
    void createPhonenumberWithExistingId() throws Exception {
        // Create the Phonenumber with an existing ID
        phonenumber.setId(1L);
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        int databaseSizeBeforeCreate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        // set the field null
        phonenumber.setCode(null);

        // Create the Phonenumber, which fails.
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkContactNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        // set the field null
        phonenumber.setContactNumber(null);

        // Create the Phonenumber, which fails.
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPhonenumberTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        // set the field null
        phonenumber.setPhonenumberType(null);

        // Create the Phonenumber, which fails.
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPhonenumbers() {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();

        // Get all the phonenumberList
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
            .value(hasItem(phonenumber.getId().intValue()))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].phonenumberType")
            .value(hasItem(DEFAULT_PHONENUMBER_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhonenumbersWithEagerRelationshipsIsEnabled() {
        when(phonenumberServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(phonenumberServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhonenumbersWithEagerRelationshipsIsNotEnabled() {
        when(phonenumberServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(phonenumberRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPhonenumber() {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();

        // Get the phonenumber
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, phonenumber.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(phonenumber.getId().intValue()))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.contactNumber")
            .value(is(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.phonenumberType")
            .value(is(DEFAULT_PHONENUMBER_TYPE.toString()));
    }

    @Test
    void getNonExistingPhonenumber() {
        // Get the phonenumber
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPhonenumber() throws Exception {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();

        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        phonenumberSearchRepository.save(phonenumber).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());

        // Update the phonenumber
        Phonenumber updatedPhonenumber = phonenumberRepository.findById(phonenumber.getId()).block();
        updatedPhonenumber
            .country(UPDATED_COUNTRY)
            .code(UPDATED_CODE)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .phonenumberType(UPDATED_PHONENUMBER_TYPE);
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(updatedPhonenumber);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, phonenumberDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        Phonenumber testPhonenumber = phonenumberList.get(phonenumberList.size() - 1);
        assertThat(testPhonenumber.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPhonenumber.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPhonenumber.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testPhonenumber.getPhonenumberType()).isEqualTo(UPDATED_PHONENUMBER_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Phonenumber> phonenumberSearchList = IterableUtils.toList(phonenumberSearchRepository.findAll().collectList().block());
                Phonenumber testPhonenumberSearch = phonenumberSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPhonenumberSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
                assertThat(testPhonenumberSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testPhonenumberSearch.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
                assertThat(testPhonenumberSearch.getPhonenumberType()).isEqualTo(UPDATED_PHONENUMBER_TYPE);
            });
    }

    @Test
    void putNonExistingPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, phonenumberDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePhonenumberWithPatch() throws Exception {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();

        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();

        // Update the phonenumber using partial update
        Phonenumber partialUpdatedPhonenumber = new Phonenumber();
        partialUpdatedPhonenumber.setId(phonenumber.getId());

        partialUpdatedPhonenumber.country(UPDATED_COUNTRY).contactNumber(UPDATED_CONTACT_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPhonenumber.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPhonenumber))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        Phonenumber testPhonenumber = phonenumberList.get(phonenumberList.size() - 1);
        assertThat(testPhonenumber.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPhonenumber.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPhonenumber.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testPhonenumber.getPhonenumberType()).isEqualTo(DEFAULT_PHONENUMBER_TYPE);
    }

    @Test
    void fullUpdatePhonenumberWithPatch() throws Exception {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();

        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();

        // Update the phonenumber using partial update
        Phonenumber partialUpdatedPhonenumber = new Phonenumber();
        partialUpdatedPhonenumber.setId(phonenumber.getId());

        partialUpdatedPhonenumber
            .country(UPDATED_COUNTRY)
            .code(UPDATED_CODE)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .phonenumberType(UPDATED_PHONENUMBER_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPhonenumber.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPhonenumber))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        Phonenumber testPhonenumber = phonenumberList.get(phonenumberList.size() - 1);
        assertThat(testPhonenumber.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPhonenumber.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPhonenumber.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testPhonenumber.getPhonenumberType()).isEqualTo(UPDATED_PHONENUMBER_TYPE);
    }

    @Test
    void patchNonExistingPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, phonenumberDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPhonenumber() throws Exception {
        int databaseSizeBeforeUpdate = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        phonenumber.setId(count.incrementAndGet());

        // Create the Phonenumber
        PhonenumberDTO phonenumberDTO = phonenumberMapper.toDto(phonenumber);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(phonenumberDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Phonenumber in the database
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePhonenumber() {
        // Initialize the database
        phonenumberRepository.save(phonenumber).block();
        phonenumberRepository.save(phonenumber).block();
        phonenumberSearchRepository.save(phonenumber).block();

        int databaseSizeBeforeDelete = phonenumberRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the phonenumber
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, phonenumber.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Phonenumber> phonenumberList = phonenumberRepository.findAll().collectList().block();
        assertThat(phonenumberList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(phonenumberSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPhonenumber() {
        // Initialize the database
        phonenumber = phonenumberRepository.save(phonenumber).block();
        phonenumberSearchRepository.save(phonenumber).block();

        // Search the phonenumber
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + phonenumber.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(phonenumber.getId().intValue()))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].contactNumber")
            .value(hasItem(DEFAULT_CONTACT_NUMBER))
            .jsonPath("$.[*].phonenumberType")
            .value(hasItem(DEFAULT_PHONENUMBER_TYPE.toString()));
    }
}
