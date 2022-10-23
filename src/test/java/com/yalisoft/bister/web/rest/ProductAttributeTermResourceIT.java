package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductAttribute;
import com.yalisoft.bister.domain.ProductAttributeTerm;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductAttributeTermRepository;
import com.yalisoft.bister.repository.search.ProductAttributeTermSearchRepository;
import com.yalisoft.bister.service.ProductAttributeTermService;
import com.yalisoft.bister.service.dto.ProductAttributeTermDTO;
import com.yalisoft.bister.service.mapper.ProductAttributeTermMapper;
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
 * Integration tests for the {@link ProductAttributeTermResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductAttributeTermResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_MENU_ORDER = 1;
    private static final Integer UPDATED_MENU_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/product-attribute-terms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-attribute-terms";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductAttributeTermRepository productAttributeTermRepository;

    @Mock
    private ProductAttributeTermRepository productAttributeTermRepositoryMock;

    @Autowired
    private ProductAttributeTermMapper productAttributeTermMapper;

    @Mock
    private ProductAttributeTermService productAttributeTermServiceMock;

    @Autowired
    private ProductAttributeTermSearchRepository productAttributeTermSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductAttributeTerm productAttributeTerm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAttributeTerm createEntity(EntityManager em) {
        ProductAttributeTerm productAttributeTerm = new ProductAttributeTerm()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .menuOrder(DEFAULT_MENU_ORDER);
        // Add required entity
        ProductAttribute productAttribute;
        productAttribute = em.insert(ProductAttributeResourceIT.createEntity(em)).block();
        productAttributeTerm.setProductAttribute(productAttribute);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productAttributeTerm.setProduct(product);
        return productAttributeTerm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAttributeTerm createUpdatedEntity(EntityManager em) {
        ProductAttributeTerm productAttributeTerm = new ProductAttributeTerm()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .menuOrder(UPDATED_MENU_ORDER);
        // Add required entity
        ProductAttribute productAttribute;
        productAttribute = em.insert(ProductAttributeResourceIT.createUpdatedEntity(em)).block();
        productAttributeTerm.setProductAttribute(productAttribute);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productAttributeTerm.setProduct(product);
        return productAttributeTerm;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductAttributeTerm.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductAttributeResourceIT.deleteEntities(em);
        ProductResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        productAttributeTermSearchRepository.deleteAll().block();
        assertThat(productAttributeTermSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productAttributeTerm = createEntity(em);
    }

    @Test
    void createProductAttributeTerm() throws Exception {
        int databaseSizeBeforeCreate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductAttributeTerm testProductAttributeTerm = productAttributeTermList.get(productAttributeTermList.size() - 1);
        assertThat(testProductAttributeTerm.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductAttributeTerm.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductAttributeTerm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductAttributeTerm.getMenuOrder()).isEqualTo(DEFAULT_MENU_ORDER);
    }

    @Test
    void createProductAttributeTermWithExistingId() throws Exception {
        // Create the ProductAttributeTerm with an existing ID
        productAttributeTerm.setId(1L);
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        int databaseSizeBeforeCreate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productAttributeTerm.setName(null);

        // Create the ProductAttributeTerm, which fails.
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productAttributeTerm.setSlug(null);

        // Create the ProductAttributeTerm, which fails.
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productAttributeTerm.setDescription(null);

        // Create the ProductAttributeTerm, which fails.
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkMenuOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        // set the field null
        productAttributeTerm.setMenuOrder(null);

        // Create the ProductAttributeTerm, which fails.
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductAttributeTerms() {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();

        // Get all the productAttributeTermList
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
            .value(hasItem(productAttributeTerm.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].menuOrder")
            .value(hasItem(DEFAULT_MENU_ORDER));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductAttributeTermsWithEagerRelationshipsIsEnabled() {
        when(productAttributeTermServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productAttributeTermServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductAttributeTermsWithEagerRelationshipsIsNotEnabled() {
        when(productAttributeTermServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productAttributeTermRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductAttributeTerm() {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();

        // Get the productAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productAttributeTerm.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productAttributeTerm.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.menuOrder")
            .value(is(DEFAULT_MENU_ORDER));
    }

    @Test
    void getNonExistingProductAttributeTerm() {
        // Get the productAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductAttributeTerm() throws Exception {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();

        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        productAttributeTermSearchRepository.save(productAttributeTerm).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());

        // Update the productAttributeTerm
        ProductAttributeTerm updatedProductAttributeTerm = productAttributeTermRepository.findById(productAttributeTerm.getId()).block();
        updatedProductAttributeTerm.name(UPDATED_NAME).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION).menuOrder(UPDATED_MENU_ORDER);
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(updatedProductAttributeTerm);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productAttributeTermDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductAttributeTerm testProductAttributeTerm = productAttributeTermList.get(productAttributeTermList.size() - 1);
        assertThat(testProductAttributeTerm.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductAttributeTerm.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductAttributeTerm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductAttributeTerm.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductAttributeTerm> productAttributeTermSearchList = IterableUtils.toList(
                    productAttributeTermSearchRepository.findAll().collectList().block()
                );
                ProductAttributeTerm testProductAttributeTermSearch = productAttributeTermSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductAttributeTermSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProductAttributeTermSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testProductAttributeTermSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductAttributeTermSearch.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
            });
    }

    @Test
    void putNonExistingProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productAttributeTermDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductAttributeTermWithPatch() throws Exception {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();

        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();

        // Update the productAttributeTerm using partial update
        ProductAttributeTerm partialUpdatedProductAttributeTerm = new ProductAttributeTerm();
        partialUpdatedProductAttributeTerm.setId(productAttributeTerm.getId());

        partialUpdatedProductAttributeTerm.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductAttributeTerm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductAttributeTerm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductAttributeTerm testProductAttributeTerm = productAttributeTermList.get(productAttributeTermList.size() - 1);
        assertThat(testProductAttributeTerm.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductAttributeTerm.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductAttributeTerm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductAttributeTerm.getMenuOrder()).isEqualTo(DEFAULT_MENU_ORDER);
    }

    @Test
    void fullUpdateProductAttributeTermWithPatch() throws Exception {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();

        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();

        // Update the productAttributeTerm using partial update
        ProductAttributeTerm partialUpdatedProductAttributeTerm = new ProductAttributeTerm();
        partialUpdatedProductAttributeTerm.setId(productAttributeTerm.getId());

        partialUpdatedProductAttributeTerm
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .menuOrder(UPDATED_MENU_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductAttributeTerm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductAttributeTerm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        ProductAttributeTerm testProductAttributeTerm = productAttributeTermList.get(productAttributeTermList.size() - 1);
        assertThat(testProductAttributeTerm.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductAttributeTerm.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductAttributeTerm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductAttributeTerm.getMenuOrder()).isEqualTo(UPDATED_MENU_ORDER);
    }

    @Test
    void patchNonExistingProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productAttributeTermDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductAttributeTerm() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        productAttributeTerm.setId(count.incrementAndGet());

        // Create the ProductAttributeTerm
        ProductAttributeTermDTO productAttributeTermDTO = productAttributeTermMapper.toDto(productAttributeTerm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeTermDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductAttributeTerm in the database
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductAttributeTerm() {
        // Initialize the database
        productAttributeTermRepository.save(productAttributeTerm).block();
        productAttributeTermRepository.save(productAttributeTerm).block();
        productAttributeTermSearchRepository.save(productAttributeTerm).block();

        int databaseSizeBeforeDelete = productAttributeTermRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productAttributeTerm
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productAttributeTerm.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductAttributeTerm> productAttributeTermList = productAttributeTermRepository.findAll().collectList().block();
        assertThat(productAttributeTermList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeTermSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductAttributeTerm() {
        // Initialize the database
        productAttributeTerm = productAttributeTermRepository.save(productAttributeTerm).block();
        productAttributeTermSearchRepository.save(productAttributeTerm).block();

        // Search the productAttributeTerm
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productAttributeTerm.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productAttributeTerm.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].menuOrder")
            .value(hasItem(DEFAULT_MENU_ORDER));
    }
}
