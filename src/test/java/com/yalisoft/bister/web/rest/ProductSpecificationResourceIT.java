package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductSpecification;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductSpecificationRepository;
import com.yalisoft.bister.repository.search.ProductSpecificationSearchRepository;
import com.yalisoft.bister.service.ProductSpecificationService;
import com.yalisoft.bister.service.dto.ProductSpecificationDTO;
import com.yalisoft.bister.service.mapper.ProductSpecificationMapper;
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
 * Integration tests for the {@link ProductSpecificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductSpecificationResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-specifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-specifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductSpecificationRepository productSpecificationRepository;

    @Mock
    private ProductSpecificationRepository productSpecificationRepositoryMock;

    @Autowired
    private ProductSpecificationMapper productSpecificationMapper;

    @Mock
    private ProductSpecificationService productSpecificationServiceMock;

    @Autowired
    private ProductSpecificationSearchRepository productSpecificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductSpecification productSpecification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecification createEntity(EntityManager em) {
        ProductSpecification productSpecification = new ProductSpecification()
            .title(DEFAULT_TITLE)
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productSpecification.setProduct(product);
        return productSpecification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecification createUpdatedEntity(EntityManager em) {
        ProductSpecification productSpecification = new ProductSpecification()
            .title(UPDATED_TITLE)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productSpecification.setProduct(product);
        return productSpecification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductSpecification.class).block();
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
        productSpecificationSearchRepository.deleteAll().block();
        assertThat(productSpecificationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productSpecification = createEntity(em);
    }

    @Test
    void createProductSpecification() throws Exception {
        int databaseSizeBeforeCreate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductSpecification testProductSpecification = productSpecificationList.get(productSpecificationList.size() - 1);
        assertThat(testProductSpecification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProductSpecification.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProductSpecification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createProductSpecificationWithExistingId() throws Exception {
        // Create the ProductSpecification with an existing ID
        productSpecification.setId(1L);
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        int databaseSizeBeforeCreate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        // set the field null
        productSpecification.setTitle(null);

        // Create the ProductSpecification, which fails.
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        // set the field null
        productSpecification.setValue(null);

        // Create the ProductSpecification, which fails.
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductSpecifications() {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();

        // Get all the productSpecificationList
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
            .value(hasItem(productSpecification.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductSpecificationsWithEagerRelationshipsIsEnabled() {
        when(productSpecificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productSpecificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductSpecificationsWithEagerRelationshipsIsNotEnabled() {
        when(productSpecificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productSpecificationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductSpecification() {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();

        // Get the productSpecification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productSpecification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productSpecification.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.value")
            .value(is(DEFAULT_VALUE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingProductSpecification() {
        // Get the productSpecification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductSpecification() throws Exception {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();

        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        productSpecificationSearchRepository.save(productSpecification).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());

        // Update the productSpecification
        ProductSpecification updatedProductSpecification = productSpecificationRepository.findById(productSpecification.getId()).block();
        updatedProductSpecification.title(UPDATED_TITLE).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(updatedProductSpecification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productSpecificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecification testProductSpecification = productSpecificationList.get(productSpecificationList.size() - 1);
        assertThat(testProductSpecification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductSpecification> productSpecificationSearchList = IterableUtils.toList(
                    productSpecificationSearchRepository.findAll().collectList().block()
                );
                ProductSpecification testProductSpecificationSearch = productSpecificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductSpecificationSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProductSpecificationSearch.getValue()).isEqualTo(UPDATED_VALUE);
                assertThat(testProductSpecificationSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productSpecificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductSpecificationWithPatch() throws Exception {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();

        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();

        // Update the productSpecification using partial update
        ProductSpecification partialUpdatedProductSpecification = new ProductSpecification();
        partialUpdatedProductSpecification.setId(productSpecification.getId());

        partialUpdatedProductSpecification.value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductSpecification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecification testProductSpecification = productSpecificationList.get(productSpecificationList.size() - 1);
        assertThat(testProductSpecification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProductSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateProductSpecificationWithPatch() throws Exception {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();

        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();

        // Update the productSpecification using partial update
        ProductSpecification partialUpdatedProductSpecification = new ProductSpecification();
        partialUpdatedProductSpecification.setId(productSpecification.getId());

        partialUpdatedProductSpecification.title(UPDATED_TITLE).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductSpecification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecification testProductSpecification = productSpecificationList.get(productSpecificationList.size() - 1);
        assertThat(testProductSpecification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productSpecificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductSpecification() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        productSpecification.setId(count.incrementAndGet());

        // Create the ProductSpecification
        ProductSpecificationDTO productSpecificationDTO = productSpecificationMapper.toDto(productSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductSpecification in the database
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductSpecification() {
        // Initialize the database
        productSpecificationRepository.save(productSpecification).block();
        productSpecificationRepository.save(productSpecification).block();
        productSpecificationSearchRepository.save(productSpecification).block();

        int databaseSizeBeforeDelete = productSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productSpecification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productSpecification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductSpecification> productSpecificationList = productSpecificationRepository.findAll().collectList().block();
        assertThat(productSpecificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductSpecification() {
        // Initialize the database
        productSpecification = productSpecificationRepository.save(productSpecification).block();
        productSpecificationSearchRepository.save(productSpecification).block();

        // Search the productSpecification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productSpecification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productSpecification.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
