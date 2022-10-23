package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.TaxClassRepository;
import com.yalisoft.bister.repository.search.TaxClassSearchRepository;
import com.yalisoft.bister.service.dto.TaxClassDTO;
import com.yalisoft.bister.service.mapper.TaxClassMapper;
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
 * Integration tests for the {@link TaxClassResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TaxClassResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tax-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tax-classes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaxClassRepository taxClassRepository;

    @Autowired
    private TaxClassMapper taxClassMapper;

    @Autowired
    private TaxClassSearchRepository taxClassSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TaxClass taxClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxClass createEntity(EntityManager em) {
        TaxClass taxClass = new TaxClass().name(DEFAULT_NAME).slug(DEFAULT_SLUG);
        return taxClass;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxClass createUpdatedEntity(EntityManager em) {
        TaxClass taxClass = new TaxClass().name(UPDATED_NAME).slug(UPDATED_SLUG);
        return taxClass;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TaxClass.class).block();
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
        taxClassSearchRepository.deleteAll().block();
        assertThat(taxClassSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        taxClass = createEntity(em);
    }

    @Test
    void createTaxClass() throws Exception {
        int databaseSizeBeforeCreate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TaxClass testTaxClass = taxClassList.get(taxClassList.size() - 1);
        assertThat(testTaxClass.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaxClass.getSlug()).isEqualTo(DEFAULT_SLUG);
    }

    @Test
    void createTaxClassWithExistingId() throws Exception {
        // Create the TaxClass with an existing ID
        taxClass.setId(1L);
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        int databaseSizeBeforeCreate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        // set the field null
        taxClass.setName(null);

        // Create the TaxClass, which fails.
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        // set the field null
        taxClass.setSlug(null);

        // Create the TaxClass, which fails.
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTaxClasses() {
        // Initialize the database
        taxClassRepository.save(taxClass).block();

        // Get all the taxClassList
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
            .value(hasItem(taxClass.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG));
    }

    @Test
    void getTaxClass() {
        // Initialize the database
        taxClassRepository.save(taxClass).block();

        // Get the taxClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, taxClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(taxClass.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG));
    }

    @Test
    void getNonExistingTaxClass() {
        // Get the taxClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTaxClass() throws Exception {
        // Initialize the database
        taxClassRepository.save(taxClass).block();

        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        taxClassSearchRepository.save(taxClass).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());

        // Update the taxClass
        TaxClass updatedTaxClass = taxClassRepository.findById(taxClass.getId()).block();
        updatedTaxClass.name(UPDATED_NAME).slug(UPDATED_SLUG);
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(updatedTaxClass);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxClassDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        TaxClass testTaxClass = taxClassList.get(taxClassList.size() - 1);
        assertThat(testTaxClass.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxClass.getSlug()).isEqualTo(UPDATED_SLUG);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TaxClass> taxClassSearchList = IterableUtils.toList(taxClassSearchRepository.findAll().collectList().block());
                TaxClass testTaxClassSearch = taxClassSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTaxClassSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testTaxClassSearch.getSlug()).isEqualTo(UPDATED_SLUG);
            });
    }

    @Test
    void putNonExistingTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxClassDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTaxClassWithPatch() throws Exception {
        // Initialize the database
        taxClassRepository.save(taxClass).block();

        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();

        // Update the taxClass using partial update
        TaxClass partialUpdatedTaxClass = new TaxClass();
        partialUpdatedTaxClass.setId(taxClass.getId());

        partialUpdatedTaxClass.name(UPDATED_NAME).slug(UPDATED_SLUG);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        TaxClass testTaxClass = taxClassList.get(taxClassList.size() - 1);
        assertThat(testTaxClass.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxClass.getSlug()).isEqualTo(UPDATED_SLUG);
    }

    @Test
    void fullUpdateTaxClassWithPatch() throws Exception {
        // Initialize the database
        taxClassRepository.save(taxClass).block();

        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();

        // Update the taxClass using partial update
        TaxClass partialUpdatedTaxClass = new TaxClass();
        partialUpdatedTaxClass.setId(taxClass.getId());

        partialUpdatedTaxClass.name(UPDATED_NAME).slug(UPDATED_SLUG);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        TaxClass testTaxClass = taxClassList.get(taxClassList.size() - 1);
        assertThat(testTaxClass.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxClass.getSlug()).isEqualTo(UPDATED_SLUG);
    }

    @Test
    void patchNonExistingTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, taxClassDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTaxClass() throws Exception {
        int databaseSizeBeforeUpdate = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        taxClass.setId(count.incrementAndGet());

        // Create the TaxClass
        TaxClassDTO taxClassDTO = taxClassMapper.toDto(taxClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(taxClassDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxClass in the database
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTaxClass() {
        // Initialize the database
        taxClassRepository.save(taxClass).block();
        taxClassRepository.save(taxClass).block();
        taxClassSearchRepository.save(taxClass).block();

        int databaseSizeBeforeDelete = taxClassRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the taxClass
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, taxClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TaxClass> taxClassList = taxClassRepository.findAll().collectList().block();
        assertThat(taxClassList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxClassSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTaxClass() {
        // Initialize the database
        taxClass = taxClassRepository.save(taxClass).block();
        taxClassSearchRepository.save(taxClass).block();

        // Search the taxClass
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + taxClass.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(taxClass.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG));
    }
}
