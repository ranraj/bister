package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductReview;
import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductReviewRepository;
import com.yalisoft.bister.repository.search.ProductReviewSearchRepository;
import com.yalisoft.bister.service.ProductReviewService;
import com.yalisoft.bister.service.dto.ProductReviewDTO;
import com.yalisoft.bister.service.mapper.ProductReviewMapper;
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
 * Integration tests for the {@link ProductReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductReviewResourceIT {

    private static final String DEFAULT_REVIEWER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_REVIEWER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REVIEWER_EMAIL = "XTz,@!.NQ6";
    private static final String UPDATED_REVIEWER_EMAIL = "d@}2v.L?";

    private static final String DEFAULT_REVIEW = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final ReviewStatus DEFAULT_STATUS = ReviewStatus.APPROVED;
    private static final ReviewStatus UPDATED_STATUS = ReviewStatus.HOLD;

    private static final Long DEFAULT_REVIEWER_ID = 1L;
    private static final Long UPDATED_REVIEWER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/product-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-reviews";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductReviewRepository productReviewRepositoryMock;

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Mock
    private ProductReviewService productReviewServiceMock;

    @Autowired
    private ProductReviewSearchRepository productReviewSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductReview productReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductReview createEntity(EntityManager em) {
        ProductReview productReview = new ProductReview()
            .reviewerName(DEFAULT_REVIEWER_NAME)
            .reviewerEmail(DEFAULT_REVIEWER_EMAIL)
            .review(DEFAULT_REVIEW)
            .rating(DEFAULT_RATING)
            .status(DEFAULT_STATUS)
            .reviewerId(DEFAULT_REVIEWER_ID);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productReview.setProduct(product);
        return productReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductReview createUpdatedEntity(EntityManager em) {
        ProductReview productReview = new ProductReview()
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productReview.setProduct(product);
        return productReview;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductReview.class).block();
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
        productReviewSearchRepository.deleteAll().block();
        assertThat(productReviewSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productReview = createEntity(em);
    }

    @Test
    void createProductReview() throws Exception {
        int databaseSizeBeforeCreate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductReview testProductReview = productReviewList.get(productReviewList.size() - 1);
        assertThat(testProductReview.getReviewerName()).isEqualTo(DEFAULT_REVIEWER_NAME);
        assertThat(testProductReview.getReviewerEmail()).isEqualTo(DEFAULT_REVIEWER_EMAIL);
        assertThat(testProductReview.getReview()).isEqualTo(DEFAULT_REVIEW);
        assertThat(testProductReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProductReview.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProductReview.getReviewerId()).isEqualTo(DEFAULT_REVIEWER_ID);
    }

    @Test
    void createProductReviewWithExistingId() throws Exception {
        // Create the ProductReview with an existing ID
        productReview.setId(1L);
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        int databaseSizeBeforeCreate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setReviewerName(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setReviewerEmail(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setReview(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setRating(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setStatus(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        // set the field null
        productReview.setReviewerId(null);

        // Create the ProductReview, which fails.
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductReviews() {
        // Initialize the database
        productReviewRepository.save(productReview).block();

        // Get all the productReviewList
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
            .value(hasItem(productReview.getId().intValue()))
            .jsonPath("$.[*].reviewerName")
            .value(hasItem(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.[*].reviewerEmail")
            .value(hasItem(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.[*].review")
            .value(hasItem(DEFAULT_REVIEW))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reviewerId")
            .value(hasItem(DEFAULT_REVIEWER_ID.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductReviewsWithEagerRelationshipsIsEnabled() {
        when(productReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productReviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductReviewsWithEagerRelationshipsIsNotEnabled() {
        when(productReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productReviewRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductReview() {
        // Initialize the database
        productReviewRepository.save(productReview).block();

        // Get the productReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productReview.getId().intValue()))
            .jsonPath("$.reviewerName")
            .value(is(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.reviewerEmail")
            .value(is(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.review")
            .value(is(DEFAULT_REVIEW))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.reviewerId")
            .value(is(DEFAULT_REVIEWER_ID.intValue()));
    }

    @Test
    void getNonExistingProductReview() {
        // Get the productReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductReview() throws Exception {
        // Initialize the database
        productReviewRepository.save(productReview).block();

        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        productReviewSearchRepository.save(productReview).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());

        // Update the productReview
        ProductReview updatedProductReview = productReviewRepository.findById(productReview.getId()).block();
        updatedProductReview
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(updatedProductReview);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        ProductReview testProductReview = productReviewList.get(productReviewList.size() - 1);
        assertThat(testProductReview.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
        assertThat(testProductReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProductReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProductReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProductReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProductReview.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductReview> productReviewSearchList = IterableUtils.toList(
                    productReviewSearchRepository.findAll().collectList().block()
                );
                ProductReview testProductReviewSearch = productReviewSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductReviewSearch.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
                assertThat(testProductReviewSearch.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
                assertThat(testProductReviewSearch.getReview()).isEqualTo(UPDATED_REVIEW);
                assertThat(testProductReviewSearch.getRating()).isEqualTo(UPDATED_RATING);
                assertThat(testProductReviewSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testProductReviewSearch.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
            });
    }

    @Test
    void putNonExistingProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductReviewWithPatch() throws Exception {
        // Initialize the database
        productReviewRepository.save(productReview).block();

        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();

        // Update the productReview using partial update
        ProductReview partialUpdatedProductReview = new ProductReview();
        partialUpdatedProductReview.setId(productReview.getId());

        partialUpdatedProductReview.reviewerEmail(UPDATED_REVIEWER_EMAIL).review(UPDATED_REVIEW).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        ProductReview testProductReview = productReviewList.get(productReviewList.size() - 1);
        assertThat(testProductReview.getReviewerName()).isEqualTo(DEFAULT_REVIEWER_NAME);
        assertThat(testProductReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProductReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProductReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProductReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProductReview.getReviewerId()).isEqualTo(DEFAULT_REVIEWER_ID);
    }

    @Test
    void fullUpdateProductReviewWithPatch() throws Exception {
        // Initialize the database
        productReviewRepository.save(productReview).block();

        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();

        // Update the productReview using partial update
        ProductReview partialUpdatedProductReview = new ProductReview();
        partialUpdatedProductReview.setId(productReview.getId());

        partialUpdatedProductReview
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        ProductReview testProductReview = productReviewList.get(productReviewList.size() - 1);
        assertThat(testProductReview.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
        assertThat(testProductReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProductReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProductReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProductReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProductReview.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
    }

    @Test
    void patchNonExistingProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productReviewDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductReview() throws Exception {
        int databaseSizeBeforeUpdate = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        productReview.setId(count.incrementAndGet());

        // Create the ProductReview
        ProductReviewDTO productReviewDTO = productReviewMapper.toDto(productReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductReview in the database
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductReview() {
        // Initialize the database
        productReviewRepository.save(productReview).block();
        productReviewRepository.save(productReview).block();
        productReviewSearchRepository.save(productReview).block();

        int databaseSizeBeforeDelete = productReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productReview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductReview> productReviewList = productReviewRepository.findAll().collectList().block();
        assertThat(productReviewList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductReview() {
        // Initialize the database
        productReview = productReviewRepository.save(productReview).block();
        productReviewSearchRepository.save(productReview).block();

        // Search the productReview
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productReview.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productReview.getId().intValue()))
            .jsonPath("$.[*].reviewerName")
            .value(hasItem(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.[*].reviewerEmail")
            .value(hasItem(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.[*].review")
            .value(hasItem(DEFAULT_REVIEW))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reviewerId")
            .value(hasItem(DEFAULT_REVIEWER_ID.intValue()));
    }
}
