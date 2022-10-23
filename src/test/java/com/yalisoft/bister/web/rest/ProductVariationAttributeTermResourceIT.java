package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductVariationAttributeTermRepository;
import com.yalisoft.bister.repository.search.ProductVariationAttributeTermSearchRepository;
import com.yalisoft.bister.service.ProductVariationAttributeTermService;
import com.yalisoft.bister.service.dto.ProductVariationAttributeTermDTO;
import com.yalisoft.bister.service.mapper.ProductVariationAttributeTermMapper;
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
 * Integration tests for the {@link ProductVariationAttributeTermResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductVariationAttributeTermResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MENU_ORDER = 1;
    private static final Integer UPDATED_MENU_ORDER = 2;

    private static final Boolean DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE = false;
    private static final Boolean UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE = true;

    private static final String ENTITY_API_URL = "/api/product-variation-attribute-terms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-variation-attribute-terms";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductVariationAttributeTermRepository productVariationAttributeTermRepository;

    @Mock
    private ProductVariationAttributeTermRepository productVariationAttributeTermRepositoryMock;

    @Autowired
    private ProductVariationAttributeTermMapper productVariationAttributeTermMapper;

    @Mock
    private ProductVariationAttributeTermService productVariationAttributeTermServiceMock;

    @Autowired
    private ProductVariationAttributeTermSearchRepository productVariationAttributeTermSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductVariationAttributeTerm productVariationAttributeTerm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariationAttributeTerm createEntity(EntityManager em) {
        ProductVariationAttributeTerm productVariationAttributeTerm = new ProductVariationAttributeTerm()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .menuOrder(DEFAULT_MENU_ORDER)
            .overRideProductAttribute(DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE);
        return productVariationAttributeTerm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariationAttributeTerm createUpdatedEntity(EntityManager em) {
        ProductVariationAttributeTerm productVariationAttributeTerm = new ProductVariationAttributeTerm()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .menuOrder(UPDATED_MENU_ORDER)
            .overRideProductAttribute(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
        return productVariationAttributeTerm;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductVariationAttributeTerm.class).block();
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
        productVariationAttributeTermSearchRepository.deleteAll().block();
        assertThat(productVariationAttributeTermSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productVariationAttributeTerm = createEntity(em);
    }

    @Test
    void createProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeCreate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    productVariationAttributeTermSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductVariationAttributeTerm testProductVariationAttributeTerm = productVariationAttributeTermList.get(
            productVariationAttributeTermList.size() - 1
        );
        assertThat(testProductVariationAttributeTerm.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductVariationAttributeTerm.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductVariationAttributeTerm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductVariationAttributeTerm.getMenuOrder()).isEqualTo(DEFAULT_MENU_ORDER);
        assertThat(testProductVariationAttributeTerm.getOverRideProductAttribute()).isEqualTo(DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE);
    }

    @Test
    void createProductVariationAttributeTermWithExistingId() throws Exception {
        // Create the ProductVariationAttributeTerm with an existing ID
        productVariationAttributeTerm.setId(1L);
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        int databaseSizeBeforeCreate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productVariationAttributeTerm.setName(null);

        // Create the ProductVariationAttributeTerm, which fails.
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productVariationAttributeTerm.setSlug(null);

        // Create the ProductVariationAttributeTerm, which fails.
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productVariationAttributeTerm.setDescription(null);

        // Create the ProductVariationAttributeTerm, which fails.
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkMenuOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productVariationAttributeTerm.setMenuOrder(null);

        // Create the ProductVariationAttributeTerm, which fails.
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductVariationAttributeTerms() {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();

        // Get all the productVariationAttributeTermList
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
            .value(hasItem(productVariationAttributeTerm.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].menuOrder")
            .value(hasItem(DEFAULT_MENU_ORDER))
            .jsonPath("$.[*].overRideProductAttribute")
            .value(hasItem(DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE.booleanValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariationAttributeTermsWithEagerRelationshipsIsEnabled() {
        when(productVariationAttributeTermServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productVariationAttributeTermServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariationAttributeTermsWithEagerRelationshipsIsNotEnabled() {
        when(productVariationAttributeTermServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productVariationAttributeTermRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductVariationAttributeTerm() {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();

        // Get the productVariationAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productVariationAttributeTerm.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productVariationAttributeTerm.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.menuOrder")
            .value(is(DEFAULT_MENU_ORDER))
            .jsonPath("$.overRideProductAttribute")
            .value(is(DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE.booleanValue()));
    }

    @Test
    void getNonExistingProductVariationAttributeTerm() {
        // Get the productVariationAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductVariationAttributeTerm() throws Exception {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();

        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        productVariationAttributeTermSearchRepository.save(productVariationAttributeTerm).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());

        // Update the productVariationAttributeTerm
        ProductVariationAttributeTerm updatedProductVariationAttributeTerm = productVariationAttributeTermRepository
            .findById(productVariationAttributeTerm.getId())
            .block();
        updatedProductVariationAttributeTerm
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .menuOrder(UPDATED_MENU_ORDER)
            .overRideProductAttribute(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            updatedProductVariationAttributeTerm
        );

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productVariationAttributeTermDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductVariationAttributeTerm testProductVariationAttributeTerm = productVariationAttributeTermList.get(
            productVariationAttributeTermList.size() - 1
        );
        assertThat(testProductVariationAttributeTerm.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductVariationAttributeTerm.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductVariationAttributeTerm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductVariationAttributeTerm.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
        assertThat(testProductVariationAttributeTerm.getOverRideProductAttribute()).isEqualTo(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    productVariationAttributeTermSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductVariationAttributeTerm> productVariationAttributeTermSearchList = IterableUtils.toList(
                    productVariationAttributeTermSearchRepository.findAll().collectList().block()
                );
                ProductVariationAttributeTerm testProductVariationAttributeTermSearch = productVariationAttributeTermSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testProductVariationAttributeTermSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProductVariationAttributeTermSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testProductVariationAttributeTermSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductVariationAttributeTermSearch.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
                assertThat(testProductVariationAttributeTermSearch.getOverRideProductAttribute())
                    .isEqualTo(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
            });
    }

    @Test
    void putNonExistingProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productVariationAttributeTermDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductVariationAttributeTermWithPatch() throws Exception {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();

        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();

        // Update the productVariationAttributeTerm using partial update
        ProductVariationAttributeTerm partialUpdatedProductVariationAttributeTerm = new ProductVariationAttributeTerm();
        partialUpdatedProductVariationAttributeTerm.setId(productVariationAttributeTerm.getId());

        partialUpdatedProductVariationAttributeTerm.overRideProductAttribute(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductVariationAttributeTerm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductVariationAttributeTerm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductVariationAttributeTerm testProductVariationAttributeTerm = productVariationAttributeTermList.get(
            productVariationAttributeTermList.size() - 1
        );
        assertThat(testProductVariationAttributeTerm.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductVariationAttributeTerm.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductVariationAttributeTerm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductVariationAttributeTerm.getMenuOrder()).isEqualTo(DEFAULT_MENU_ORDER);
        assertThat(testProductVariationAttributeTerm.getOverRideProductAttribute()).isEqualTo(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
    }

    @Test
    void fullUpdateProductVariationAttributeTermWithPatch() throws Exception {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();

        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();

        // Update the productVariationAttributeTerm using partial update
        ProductVariationAttributeTerm partialUpdatedProductVariationAttributeTerm = new ProductVariationAttributeTerm();
        partialUpdatedProductVariationAttributeTerm.setId(productVariationAttributeTerm.getId());

        partialUpdatedProductVariationAttributeTerm
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .menuOrder(UPDATED_MENU_ORDER)
            .overRideProductAttribute(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductVariationAttributeTerm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductVariationAttributeTerm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductVariationAttributeTerm testProductVariationAttributeTerm = productVariationAttributeTermList.get(
            productVariationAttributeTermList.size() - 1
        );
        assertThat(testProductVariationAttributeTerm.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductVariationAttributeTerm.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductVariationAttributeTerm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductVariationAttributeTerm.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
        assertThat(testProductVariationAttributeTerm.getOverRideProductAttribute()).isEqualTo(UPDATED_OVER_RIDE_PRODUCT_ATTRIBUTE);
    }

    @Test
    void patchNonExistingProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productVariationAttributeTermDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductVariationAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        productVariationAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductVariationAttributeTerm
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = productVariationAttributeTermMapper.toDto(
            productVariationAttributeTerm
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductVariationAttributeTerm in the database
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductVariationAttributeTerm() {
        // Initialize the database
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();
        productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();
        productVariationAttributeTermSearchRepository.save(productVariationAttributeTerm).block();

        int databaseSizeBeforeDelete = productVariationAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productVariationAttributeTerm
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productVariationAttributeTerm.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductVariationAttributeTerm> productVariationAttributeTermList = productVariationAttributeTermRepository
            .findAll()
            .collectList()
            .block();
        assertThat(productVariationAttributeTermList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductVariationAttributeTerm() {
        // Initialize the database
        productVariationAttributeTerm = productVariationAttributeTermRepository.save(productVariationAttributeTerm).block();
        productVariationAttributeTermSearchRepository.save(productVariationAttributeTerm).block();

        // Search the productVariationAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productVariationAttributeTerm.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productVariationAttributeTerm.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].menuOrder")
            .value(hasItem(DEFAULT_MENU_ORDER))
            .jsonPath("$.[*].overRideProductAttribute")
            .value(hasItem(DEFAULT_OVER_RIDE_PRODUCT_ATTRIBUTE.booleanValue()));
    }
}
