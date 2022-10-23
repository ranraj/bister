package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.BusinessPartner;
import com.yalisoft.bister.repository.BusinessPartnerRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.BusinessPartnerSearchRepository;
import com.yalisoft.bister.service.dto.BusinessPartnerDTO;
import com.yalisoft.bister.service.mapper.BusinessPartnerMapper;
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
 * Integration tests for the {@link BusinessPartnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BusinessPartnerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/business-partners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/business-partners";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    private BusinessPartnerMapper businessPartnerMapper;

    @Autowired
    private BusinessPartnerSearchRepository businessPartnerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BusinessPartner businessPartner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessPartner createEntity(EntityManager em) {
        BusinessPartner businessPartner = new BusinessPartner().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).key(DEFAULT_KEY);
        return businessPartner;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessPartner createUpdatedEntity(EntityManager em) {
        BusinessPartner businessPartner = new BusinessPartner().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);
        return businessPartner;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BusinessPartner.class).block();
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
        businessPartnerSearchRepository.deleteAll().block();
        assertThat(businessPartnerSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        businessPartner = createEntity(em);
    }

    @Test
    void createBusinessPartner() throws Exception {
        int databaseSizeBeforeCreate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BusinessPartner testBusinessPartner = businessPartnerList.get(businessPartnerList.size() - 1);
        assertThat(testBusinessPartner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinessPartner.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBusinessPartner.getKey()).isEqualTo(DEFAULT_KEY);
    }

    @Test
    void createBusinessPartnerWithExistingId() throws Exception {
        // Create the BusinessPartner with an existing ID
        businessPartner.setId(1L);
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        int databaseSizeBeforeCreate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        // set the field null
        businessPartner.setName(null);

        // Create the BusinessPartner, which fails.
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        // set the field null
        businessPartner.setDescription(null);

        // Create the BusinessPartner, which fails.
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        // set the field null
        businessPartner.setKey(null);

        // Create the BusinessPartner, which fails.
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBusinessPartners() {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();

        // Get all the businessPartnerList
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
            .value(hasItem(businessPartner.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].key")
            .value(hasItem(DEFAULT_KEY));
    }

    @Test
    void getBusinessPartner() {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();

        // Get the businessPartner
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, businessPartner.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(businessPartner.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.key")
            .value(is(DEFAULT_KEY));
    }

    @Test
    void getNonExistingBusinessPartner() {
        // Get the businessPartner
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBusinessPartner() throws Exception {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();

        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        businessPartnerSearchRepository.save(businessPartner).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());

        // Update the businessPartner
        BusinessPartner updatedBusinessPartner = businessPartnerRepository.findById(businessPartner.getId()).block();
        updatedBusinessPartner.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(updatedBusinessPartner);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, businessPartnerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        BusinessPartner testBusinessPartner = businessPartnerList.get(businessPartnerList.size() - 1);
        assertThat(testBusinessPartner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessPartner.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBusinessPartner.getKey()).isEqualTo(UPDATED_KEY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BusinessPartner> businessPartnerSearchList = IterableUtils.toList(
                    businessPartnerSearchRepository.findAll().collectList().block()
                );
                BusinessPartner testBusinessPartnerSearch = businessPartnerSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBusinessPartnerSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testBusinessPartnerSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testBusinessPartnerSearch.getKey()).isEqualTo(UPDATED_KEY);
            });
    }

    @Test
    void putNonExistingBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, businessPartnerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBusinessPartnerWithPatch() throws Exception {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();

        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();

        // Update the businessPartner using partial update
        BusinessPartner partialUpdatedBusinessPartner = new BusinessPartner();
        partialUpdatedBusinessPartner.setId(businessPartner.getId());

        partialUpdatedBusinessPartner.description(UPDATED_DESCRIPTION).key(UPDATED_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBusinessPartner.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinessPartner))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        BusinessPartner testBusinessPartner = businessPartnerList.get(businessPartnerList.size() - 1);
        assertThat(testBusinessPartner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinessPartner.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBusinessPartner.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    void fullUpdateBusinessPartnerWithPatch() throws Exception {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();

        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();

        // Update the businessPartner using partial update
        BusinessPartner partialUpdatedBusinessPartner = new BusinessPartner();
        partialUpdatedBusinessPartner.setId(businessPartner.getId());

        partialUpdatedBusinessPartner.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).key(UPDATED_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBusinessPartner.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinessPartner))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        BusinessPartner testBusinessPartner = businessPartnerList.get(businessPartnerList.size() - 1);
        assertThat(testBusinessPartner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessPartner.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBusinessPartner.getKey()).isEqualTo(UPDATED_KEY);
    }

    @Test
    void patchNonExistingBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, businessPartnerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBusinessPartner() throws Exception {
        int databaseSizeBeforeUpdate = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        businessPartner.setId(count.incrementAndGet());

        // Create the BusinessPartner
        BusinessPartnerDTO businessPartnerDTO = businessPartnerMapper.toDto(businessPartner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(businessPartnerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BusinessPartner in the database
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBusinessPartner() {
        // Initialize the database
        businessPartnerRepository.save(businessPartner).block();
        businessPartnerRepository.save(businessPartner).block();
        businessPartnerSearchRepository.save(businessPartner).block();

        int databaseSizeBeforeDelete = businessPartnerRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the businessPartner
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, businessPartner.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<BusinessPartner> businessPartnerList = businessPartnerRepository.findAll().collectList().block();
        assertThat(businessPartnerList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPartnerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBusinessPartner() {
        // Initialize the database
        businessPartner = businessPartnerRepository.save(businessPartner).block();
        businessPartnerSearchRepository.save(businessPartner).block();

        // Search the businessPartner
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + businessPartner.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(businessPartner.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].key")
            .value(hasItem(DEFAULT_KEY));
    }
}
