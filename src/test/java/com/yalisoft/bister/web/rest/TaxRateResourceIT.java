package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.domain.TaxRate;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.TaxRateRepository;
import com.yalisoft.bister.repository.search.TaxRateSearchRepository;
import com.yalisoft.bister.service.TaxRateService;
import com.yalisoft.bister.service.dto.TaxRateDTO;
import com.yalisoft.bister.service.mapper.TaxRateMapper;
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
 * Integration tests for the {@link TaxRateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TaxRateResourceIT {

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_RATE = "AAAAAAAAAA";
    private static final String UPDATED_RATE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_COMPOUND = false;
    private static final Boolean UPDATED_COMPOUND = true;

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    private static final String ENTITY_API_URL = "/api/tax-rates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tax-rates";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxRateRepository taxRateRepositoryMock;

    @Autowired
    private TaxRateMapper taxRateMapper;

    @Mock
    private TaxRateService taxRateServiceMock;

    @Autowired
    private TaxRateSearchRepository taxRateSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TaxRate taxRate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxRate createEntity(EntityManager em) {
        TaxRate taxRate = new TaxRate()
            .country(DEFAULT_COUNTRY)
            .state(DEFAULT_STATE)
            .postcode(DEFAULT_POSTCODE)
            .city(DEFAULT_CITY)
            .rate(DEFAULT_RATE)
            .name(DEFAULT_NAME)
            .compound(DEFAULT_COMPOUND)
            .priority(DEFAULT_PRIORITY);
        // Add required entity
        TaxClass taxClass;
        taxClass = em.insert(TaxClassResourceIT.createEntity(em)).block();
        taxRate.setTaxClass(taxClass);
        return taxRate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxRate createUpdatedEntity(EntityManager em) {
        TaxRate taxRate = new TaxRate()
            .country(UPDATED_COUNTRY)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .city(UPDATED_CITY)
            .rate(UPDATED_RATE)
            .name(UPDATED_NAME)
            .compound(UPDATED_COMPOUND)
            .priority(UPDATED_PRIORITY);
        // Add required entity
        TaxClass taxClass;
        taxClass = em.insert(TaxClassResourceIT.createUpdatedEntity(em)).block();
        taxRate.setTaxClass(taxClass);
        return taxRate;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TaxRate.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        TaxClassResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        taxRateSearchRepository.deleteAll().block();
        assertThat(taxRateSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        taxRate = createEntity(em);
    }

    @Test
    void createTaxRate() throws Exception {
        int databaseSizeBeforeCreate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TaxRate testTaxRate = taxRateList.get(taxRateList.size() - 1);
        assertThat(testTaxRate.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testTaxRate.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testTaxRate.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testTaxRate.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testTaxRate.getRate()).isEqualTo(DEFAULT_RATE);
        assertThat(testTaxRate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaxRate.getCompound()).isEqualTo(DEFAULT_COMPOUND);
        assertThat(testTaxRate.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    void createTaxRateWithExistingId() throws Exception {
        // Create the TaxRate with an existing ID
        taxRate.setId(1L);
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        int databaseSizeBeforeCreate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setCountry(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setState(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPostcodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setPostcode(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setCity(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setRate(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setName(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCompoundIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setCompound(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPriorityIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        // set the field null
        taxRate.setPriority(null);

        // Create the TaxRate, which fails.
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTaxRates() {
        // Initialize the database
        taxRateRepository.save(taxRate).block();

        // Get all the taxRateList
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
            .value(hasItem(taxRate.getId().intValue()))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].postcode")
            .value(hasItem(DEFAULT_POSTCODE))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].rate")
            .value(hasItem(DEFAULT_RATE))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].compound")
            .value(hasItem(DEFAULT_COMPOUND.booleanValue()))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaxRatesWithEagerRelationshipsIsEnabled() {
        when(taxRateServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(taxRateServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaxRatesWithEagerRelationshipsIsNotEnabled() {
        when(taxRateServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(taxRateRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTaxRate() {
        // Initialize the database
        taxRateRepository.save(taxRate).block();

        // Get the taxRate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, taxRate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(taxRate.getId().intValue()))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.state")
            .value(is(DEFAULT_STATE))
            .jsonPath("$.postcode")
            .value(is(DEFAULT_POSTCODE))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.rate")
            .value(is(DEFAULT_RATE))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.compound")
            .value(is(DEFAULT_COMPOUND.booleanValue()))
            .jsonPath("$.priority")
            .value(is(DEFAULT_PRIORITY));
    }

    @Test
    void getNonExistingTaxRate() {
        // Get the taxRate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTaxRate() throws Exception {
        // Initialize the database
        taxRateRepository.save(taxRate).block();

        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        taxRateSearchRepository.save(taxRate).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());

        // Update the taxRate
        TaxRate updatedTaxRate = taxRateRepository.findById(taxRate.getId()).block();
        updatedTaxRate
            .country(UPDATED_COUNTRY)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .city(UPDATED_CITY)
            .rate(UPDATED_RATE)
            .name(UPDATED_NAME)
            .compound(UPDATED_COMPOUND)
            .priority(UPDATED_PRIORITY);
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(updatedTaxRate);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxRateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        TaxRate testTaxRate = taxRateList.get(taxRateList.size() - 1);
        assertThat(testTaxRate.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testTaxRate.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testTaxRate.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testTaxRate.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTaxRate.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testTaxRate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxRate.getCompound()).isEqualTo(UPDATED_COMPOUND);
        assertThat(testTaxRate.getPriority()).isEqualTo(UPDATED_PRIORITY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TaxRate> taxRateSearchList = IterableUtils.toList(taxRateSearchRepository.findAll().collectList().block());
                TaxRate testTaxRateSearch = taxRateSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTaxRateSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
                assertThat(testTaxRateSearch.getState()).isEqualTo(UPDATED_STATE);
                assertThat(testTaxRateSearch.getPostcode()).isEqualTo(UPDATED_POSTCODE);
                assertThat(testTaxRateSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testTaxRateSearch.getRate()).isEqualTo(UPDATED_RATE);
                assertThat(testTaxRateSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testTaxRateSearch.getCompound()).isEqualTo(UPDATED_COMPOUND);
                assertThat(testTaxRateSearch.getPriority()).isEqualTo(UPDATED_PRIORITY);
            });
    }

    @Test
    void putNonExistingTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxRateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTaxRateWithPatch() throws Exception {
        // Initialize the database
        taxRateRepository.save(taxRate).block();

        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();

        // Update the taxRate using partial update
        TaxRate partialUpdatedTaxRate = new TaxRate();
        partialUpdatedTaxRate.setId(taxRate.getId());

        partialUpdatedTaxRate.country(UPDATED_COUNTRY).city(UPDATED_CITY).rate(UPDATED_RATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxRate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxRate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        TaxRate testTaxRate = taxRateList.get(taxRateList.size() - 1);
        assertThat(testTaxRate.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testTaxRate.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testTaxRate.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testTaxRate.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTaxRate.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testTaxRate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaxRate.getCompound()).isEqualTo(DEFAULT_COMPOUND);
        assertThat(testTaxRate.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    void fullUpdateTaxRateWithPatch() throws Exception {
        // Initialize the database
        taxRateRepository.save(taxRate).block();

        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();

        // Update the taxRate using partial update
        TaxRate partialUpdatedTaxRate = new TaxRate();
        partialUpdatedTaxRate.setId(taxRate.getId());

        partialUpdatedTaxRate
            .country(UPDATED_COUNTRY)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .city(UPDATED_CITY)
            .rate(UPDATED_RATE)
            .name(UPDATED_NAME)
            .compound(UPDATED_COMPOUND)
            .priority(UPDATED_PRIORITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxRate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxRate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        TaxRate testTaxRate = taxRateList.get(taxRateList.size() - 1);
        assertThat(testTaxRate.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testTaxRate.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testTaxRate.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testTaxRate.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTaxRate.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testTaxRate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxRate.getCompound()).isEqualTo(UPDATED_COMPOUND);
        assertThat(testTaxRate.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    void patchNonExistingTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, taxRateDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTaxRate() throws Exception {
        int databaseSizeBeforeUpdate = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        taxRate.setId(count.incrementAndGet());

        // Create the TaxRate
        TaxRateDTO taxRateDTO = taxRateMapper.toDto(taxRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxRateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxRate in the database
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTaxRate() {
        // Initialize the database
        taxRateRepository.save(taxRate).block();
        taxRateRepository.save(taxRate).block();
        taxRateSearchRepository.save(taxRate).block();

        int databaseSizeBeforeDelete = taxRateRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the taxRate
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, taxRate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TaxRate> taxRateList = taxRateRepository.findAll().collectList().block();
        assertThat(taxRateList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxRateSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTaxRate() {
        // Initialize the database
        taxRate = taxRateRepository.save(taxRate).block();
        taxRateSearchRepository.save(taxRate).block();

        // Search the taxRate
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + taxRate.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(taxRate.getId().intValue()))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].postcode")
            .value(hasItem(DEFAULT_POSTCODE))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].rate")
            .value(hasItem(DEFAULT_RATE))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].compound")
            .value(hasItem(DEFAULT_COMPOUND.booleanValue()))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY));
    }
}
