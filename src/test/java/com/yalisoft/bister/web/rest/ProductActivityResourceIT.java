package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductActivity;
import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductActivityRepository;
import com.yalisoft.bister.repository.search.ProductActivitySearchRepository;
import com.yalisoft.bister.service.ProductActivityService;
import com.yalisoft.bister.service.dto.ProductActivityDTO;
import com.yalisoft.bister.service.mapper.ProductActivityMapper;
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
 * Integration tests for the {@link ProductActivityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductActivityResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBBBBBBBBBBBB";

    private static final ActivityStatus DEFAULT_STATUS = ActivityStatus.NEW;
    private static final ActivityStatus UPDATED_STATUS = ActivityStatus.INPROGRESS;

    private static final String ENTITY_API_URL = "/api/product-activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-activities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductActivityRepository productActivityRepository;

    @Mock
    private ProductActivityRepository productActivityRepositoryMock;

    @Autowired
    private ProductActivityMapper productActivityMapper;

    @Mock
    private ProductActivityService productActivityServiceMock;

    @Autowired
    private ProductActivitySearchRepository productActivitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductActivity productActivity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductActivity createEntity(EntityManager em) {
        ProductActivity productActivity = new ProductActivity().title(DEFAULT_TITLE).details(DEFAULT_DETAILS).status(DEFAULT_STATUS);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productActivity.setProduct(product);
        return productActivity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductActivity createUpdatedEntity(EntityManager em) {
        ProductActivity productActivity = new ProductActivity().title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productActivity.setProduct(product);
        return productActivity;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductActivity.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        productActivitySearchRepository.deleteAll().block();
        assertThat(productActivitySearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productActivity = createEntity(em);
    }

    @Test
    void createProductActivity() throws Exception {
        int databaseSizeBeforeCreate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductActivity testProductActivity = productActivityList.get(productActivityList.size() - 1);
        assertThat(testProductActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProductActivity.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testProductActivity.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createProductActivityWithExistingId() throws Exception {
        // Create the ProductActivity with an existing ID
        productActivity.setId(1L);
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        int databaseSizeBeforeCreate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        // set the field null
        productActivity.setTitle(null);

        // Create the ProductActivity, which fails.
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        // set the field null
        productActivity.setStatus(null);

        // Create the ProductActivity, which fails.
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductActivities() {
        // Initialize the database
        productActivityRepository.save(productActivity).block();

        // Get all the productActivityList
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
            .value(hasItem(productActivity.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductActivitiesWithEagerRelationshipsIsEnabled() {
        when(productActivityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productActivityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductActivitiesWithEagerRelationshipsIsNotEnabled() {
        when(productActivityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productActivityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductActivity() {
        // Initialize the database
        productActivityRepository.save(productActivity).block();

        // Get the productActivity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productActivity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productActivity.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingProductActivity() {
        // Get the productActivity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductActivity() throws Exception {
        // Initialize the database
        productActivityRepository.save(productActivity).block();

        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        productActivitySearchRepository.save(productActivity).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());

        // Update the productActivity
        ProductActivity updatedProductActivity = productActivityRepository.findById(productActivity.getId()).block();
        updatedProductActivity.title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(updatedProductActivity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productActivityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        ProductActivity testProductActivity = productActivityList.get(productActivityList.size() - 1);
        assertThat(testProductActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductActivity.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProductActivity.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductActivity> productActivitySearchList = IterableUtils.toList(
                    productActivitySearchRepository.findAll().collectList().block()
                );
                ProductActivity testProductActivitySearch = productActivitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductActivitySearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProductActivitySearch.getDetails()).isEqualTo(UPDATED_DETAILS);
                assertThat(testProductActivitySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    void putNonExistingProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productActivityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductActivityWithPatch() throws Exception {
        // Initialize the database
        productActivityRepository.save(productActivity).block();

        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();

        // Update the productActivity using partial update
        ProductActivity partialUpdatedProductActivity = new ProductActivity();
        partialUpdatedProductActivity.setId(productActivity.getId());

        partialUpdatedProductActivity.title(UPDATED_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductActivity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductActivity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        ProductActivity testProductActivity = productActivityList.get(productActivityList.size() - 1);
        assertThat(testProductActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductActivity.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testProductActivity.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateProductActivityWithPatch() throws Exception {
        // Initialize the database
        productActivityRepository.save(productActivity).block();

        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();

        // Update the productActivity using partial update
        ProductActivity partialUpdatedProductActivity = new ProductActivity();
        partialUpdatedProductActivity.setId(productActivity.getId());

        partialUpdatedProductActivity.title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductActivity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductActivity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        ProductActivity testProductActivity = productActivityList.get(productActivityList.size() - 1);
        assertThat(testProductActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductActivity.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProductActivity.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productActivityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductActivity() throws Exception {
        int databaseSizeBeforeUpdate = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        productActivity.setId(count.incrementAndGet());

        // Create the ProductActivity
        ProductActivityDTO productActivityDTO = productActivityMapper.toDto(productActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productActivityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductActivity in the database
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductActivity() {
        // Initialize the database
        productActivityRepository.save(productActivity).block();
        productActivityRepository.save(productActivity).block();
        productActivitySearchRepository.save(productActivity).block();

        int databaseSizeBeforeDelete = productActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productActivity
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productActivity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductActivity> productActivityList = productActivityRepository.findAll().collectList().block();
        assertThat(productActivityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductActivity() {
        // Initialize the database
        productActivity = productActivityRepository.save(productActivity).block();
        productActivitySearchRepository.save(productActivity).block();

        // Search the productActivity
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productActivity.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productActivity.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }
}
