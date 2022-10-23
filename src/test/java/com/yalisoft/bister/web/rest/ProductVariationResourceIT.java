package com.yalisoft.bister.web.rest;

import static com.yalisoft.bister.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.domain.enumeration.SaleStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductVariationRepository;
import com.yalisoft.bister.repository.search.ProductVariationSearchRepository;
import com.yalisoft.bister.service.ProductVariationService;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import com.yalisoft.bister.service.mapper.ProductVariationMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ProductVariationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductVariationResourceIT {

    private static final String DEFAULT_ASSET_ID = "AAAAAAAAAA";
    private static final String UPDATED_ASSET_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_REGULAR_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_REGULAR_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(2);

    private static final LocalDate DEFAULT_DATE_ON_SALE_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ON_SALE_FROM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_ON_SALE_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ON_SALE_TO = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_DRAFT = false;
    private static final Boolean UPDATED_IS_DRAFT = true;

    private static final Boolean DEFAULT_USE_PARENT_DETAILS = false;
    private static final Boolean UPDATED_USE_PARENT_DETAILS = true;

    private static final SaleStatus DEFAULT_SALE_STATUS = SaleStatus.RESALE;
    private static final SaleStatus UPDATED_SALE_STATUS = SaleStatus.SOLD;

    private static final String ENTITY_API_URL = "/api/product-variations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-variations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Mock
    private ProductVariationRepository productVariationRepositoryMock;

    @Autowired
    private ProductVariationMapper productVariationMapper;

    @Mock
    private ProductVariationService productVariationServiceMock;

    @Autowired
    private ProductVariationSearchRepository productVariationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductVariation productVariation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariation createEntity(EntityManager em) {
        ProductVariation productVariation = new ProductVariation()
            .assetId(DEFAULT_ASSET_ID)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .regularPrice(DEFAULT_REGULAR_PRICE)
            .salePrice(DEFAULT_SALE_PRICE)
            .dateOnSaleFrom(DEFAULT_DATE_ON_SALE_FROM)
            .dateOnSaleTo(DEFAULT_DATE_ON_SALE_TO)
            .isDraft(DEFAULT_IS_DRAFT)
            .useParentDetails(DEFAULT_USE_PARENT_DETAILS)
            .saleStatus(DEFAULT_SALE_STATUS);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productVariation.setProduct(product);
        return productVariation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariation createUpdatedEntity(EntityManager em) {
        ProductVariation productVariation = new ProductVariation()
            .assetId(UPDATED_ASSET_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .regularPrice(UPDATED_REGULAR_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .dateOnSaleFrom(UPDATED_DATE_ON_SALE_FROM)
            .dateOnSaleTo(UPDATED_DATE_ON_SALE_TO)
            .isDraft(UPDATED_IS_DRAFT)
            .useParentDetails(UPDATED_USE_PARENT_DETAILS)
            .saleStatus(UPDATED_SALE_STATUS);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productVariation.setProduct(product);
        return productVariation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductVariation.class).block();
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
        productVariationSearchRepository.deleteAll().block();
        assertThat(productVariationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productVariation = createEntity(em);
    }

    @Test
    void createProductVariation() throws Exception {
        int databaseSizeBeforeCreate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductVariation testProductVariation = productVariationList.get(productVariationList.size() - 1);
        assertThat(testProductVariation.getAssetId()).isEqualTo(DEFAULT_ASSET_ID);
        assertThat(testProductVariation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductVariation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductVariation.getRegularPrice()).isEqualByComparingTo(DEFAULT_REGULAR_PRICE);
        assertThat(testProductVariation.getSalePrice()).isEqualByComparingTo(DEFAULT_SALE_PRICE);
        assertThat(testProductVariation.getDateOnSaleFrom()).isEqualTo(DEFAULT_DATE_ON_SALE_FROM);
        assertThat(testProductVariation.getDateOnSaleTo()).isEqualTo(DEFAULT_DATE_ON_SALE_TO);
        assertThat(testProductVariation.getIsDraft()).isEqualTo(DEFAULT_IS_DRAFT);
        assertThat(testProductVariation.getUseParentDetails()).isEqualTo(DEFAULT_USE_PARENT_DETAILS);
        assertThat(testProductVariation.getSaleStatus()).isEqualTo(DEFAULT_SALE_STATUS);
    }

    @Test
    void createProductVariationWithExistingId() throws Exception {
        // Create the ProductVariation with an existing ID
        productVariation.setId(1L);
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        int databaseSizeBeforeCreate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setName(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setDescription(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRegularPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setRegularPrice(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSalePriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setSalePrice(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateOnSaleFromIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setDateOnSaleFrom(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateOnSaleToIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setDateOnSaleTo(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkIsDraftIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setIsDraft(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkUseParentDetailsIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setUseParentDetails(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSaleStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        // set the field null
        productVariation.setSaleStatus(null);

        // Create the ProductVariation, which fails.
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductVariations() {
        // Initialize the database
        productVariationRepository.save(productVariation).block();

        // Get all the productVariationList
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
            .value(hasItem(productVariation.getId().intValue()))
            .jsonPath("$.[*].assetId")
            .value(hasItem(DEFAULT_ASSET_ID))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].regularPrice")
            .value(hasItem(sameNumber(DEFAULT_REGULAR_PRICE)))
            .jsonPath("$.[*].salePrice")
            .value(hasItem(sameNumber(DEFAULT_SALE_PRICE)))
            .jsonPath("$.[*].dateOnSaleFrom")
            .value(hasItem(DEFAULT_DATE_ON_SALE_FROM.toString()))
            .jsonPath("$.[*].dateOnSaleTo")
            .value(hasItem(DEFAULT_DATE_ON_SALE_TO.toString()))
            .jsonPath("$.[*].isDraft")
            .value(hasItem(DEFAULT_IS_DRAFT.booleanValue()))
            .jsonPath("$.[*].useParentDetails")
            .value(hasItem(DEFAULT_USE_PARENT_DETAILS.booleanValue()))
            .jsonPath("$.[*].saleStatus")
            .value(hasItem(DEFAULT_SALE_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariationsWithEagerRelationshipsIsEnabled() {
        when(productVariationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productVariationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariationsWithEagerRelationshipsIsNotEnabled() {
        when(productVariationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productVariationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductVariation() {
        // Initialize the database
        productVariationRepository.save(productVariation).block();

        // Get the productVariation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productVariation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productVariation.getId().intValue()))
            .jsonPath("$.assetId")
            .value(is(DEFAULT_ASSET_ID))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.regularPrice")
            .value(is(sameNumber(DEFAULT_REGULAR_PRICE)))
            .jsonPath("$.salePrice")
            .value(is(sameNumber(DEFAULT_SALE_PRICE)))
            .jsonPath("$.dateOnSaleFrom")
            .value(is(DEFAULT_DATE_ON_SALE_FROM.toString()))
            .jsonPath("$.dateOnSaleTo")
            .value(is(DEFAULT_DATE_ON_SALE_TO.toString()))
            .jsonPath("$.isDraft")
            .value(is(DEFAULT_IS_DRAFT.booleanValue()))
            .jsonPath("$.useParentDetails")
            .value(is(DEFAULT_USE_PARENT_DETAILS.booleanValue()))
            .jsonPath("$.saleStatus")
            .value(is(DEFAULT_SALE_STATUS.toString()));
    }

    @Test
    void getNonExistingProductVariation() {
        // Get the productVariation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductVariation() throws Exception {
        // Initialize the database
        productVariationRepository.save(productVariation).block();

        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        productVariationSearchRepository.save(productVariation).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());

        // Update the productVariation
        ProductVariation updatedProductVariation = productVariationRepository.findById(productVariation.getId()).block();
        updatedProductVariation
            .assetId(UPDATED_ASSET_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .regularPrice(UPDATED_REGULAR_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .dateOnSaleFrom(UPDATED_DATE_ON_SALE_FROM)
            .dateOnSaleTo(UPDATED_DATE_ON_SALE_TO)
            .isDraft(UPDATED_IS_DRAFT)
            .useParentDetails(UPDATED_USE_PARENT_DETAILS)
            .saleStatus(UPDATED_SALE_STATUS);
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(updatedProductVariation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productVariationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        ProductVariation testProductVariation = productVariationList.get(productVariationList.size() - 1);
        assertThat(testProductVariation.getAssetId()).isEqualTo(UPDATED_ASSET_ID);
        assertThat(testProductVariation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductVariation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductVariation.getRegularPrice()).isEqualByComparingTo(UPDATED_REGULAR_PRICE);
        assertThat(testProductVariation.getSalePrice()).isEqualByComparingTo(UPDATED_SALE_PRICE);
        assertThat(testProductVariation.getDateOnSaleFrom()).isEqualTo(UPDATED_DATE_ON_SALE_FROM);
        assertThat(testProductVariation.getDateOnSaleTo()).isEqualTo(UPDATED_DATE_ON_SALE_TO);
        assertThat(testProductVariation.getIsDraft()).isEqualTo(UPDATED_IS_DRAFT);
        assertThat(testProductVariation.getUseParentDetails()).isEqualTo(UPDATED_USE_PARENT_DETAILS);
        assertThat(testProductVariation.getSaleStatus()).isEqualTo(UPDATED_SALE_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductVariation> productVariationSearchList = IterableUtils.toList(
                    productVariationSearchRepository.findAll().collectList().block()
                );
                ProductVariation testProductVariationSearch = productVariationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductVariationSearch.getAssetId()).isEqualTo(UPDATED_ASSET_ID);
                assertThat(testProductVariationSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProductVariationSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductVariationSearch.getRegularPrice()).isEqualByComparingTo(UPDATED_REGULAR_PRICE);
                assertThat(testProductVariationSearch.getSalePrice()).isEqualByComparingTo(UPDATED_SALE_PRICE);
                assertThat(testProductVariationSearch.getDateOnSaleFrom()).isEqualTo(UPDATED_DATE_ON_SALE_FROM);
                assertThat(testProductVariationSearch.getDateOnSaleTo()).isEqualTo(UPDATED_DATE_ON_SALE_TO);
                assertThat(testProductVariationSearch.getIsDraft()).isEqualTo(UPDATED_IS_DRAFT);
                assertThat(testProductVariationSearch.getUseParentDetails()).isEqualTo(UPDATED_USE_PARENT_DETAILS);
                assertThat(testProductVariationSearch.getSaleStatus()).isEqualTo(UPDATED_SALE_STATUS);
            });
    }

    @Test
    void putNonExistingProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productVariationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductVariationWithPatch() throws Exception {
        // Initialize the database
        productVariationRepository.save(productVariation).block();

        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();

        // Update the productVariation using partial update
        ProductVariation partialUpdatedProductVariation = new ProductVariation();
        partialUpdatedProductVariation.setId(productVariation.getId());

        partialUpdatedProductVariation
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .regularPrice(UPDATED_REGULAR_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .dateOnSaleFrom(UPDATED_DATE_ON_SALE_FROM)
            .dateOnSaleTo(UPDATED_DATE_ON_SALE_TO)
            .isDraft(UPDATED_IS_DRAFT)
            .useParentDetails(UPDATED_USE_PARENT_DETAILS)
            .saleStatus(UPDATED_SALE_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductVariation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductVariation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        ProductVariation testProductVariation = productVariationList.get(productVariationList.size() - 1);
        assertThat(testProductVariation.getAssetId()).isEqualTo(DEFAULT_ASSET_ID);
        assertThat(testProductVariation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductVariation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductVariation.getRegularPrice()).isEqualByComparingTo(UPDATED_REGULAR_PRICE);
        assertThat(testProductVariation.getSalePrice()).isEqualByComparingTo(UPDATED_SALE_PRICE);
        assertThat(testProductVariation.getDateOnSaleFrom()).isEqualTo(UPDATED_DATE_ON_SALE_FROM);
        assertThat(testProductVariation.getDateOnSaleTo()).isEqualTo(UPDATED_DATE_ON_SALE_TO);
        assertThat(testProductVariation.getIsDraft()).isEqualTo(UPDATED_IS_DRAFT);
        assertThat(testProductVariation.getUseParentDetails()).isEqualTo(UPDATED_USE_PARENT_DETAILS);
        assertThat(testProductVariation.getSaleStatus()).isEqualTo(UPDATED_SALE_STATUS);
    }

    @Test
    void fullUpdateProductVariationWithPatch() throws Exception {
        // Initialize the database
        productVariationRepository.save(productVariation).block();

        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();

        // Update the productVariation using partial update
        ProductVariation partialUpdatedProductVariation = new ProductVariation();
        partialUpdatedProductVariation.setId(productVariation.getId());

        partialUpdatedProductVariation
            .assetId(UPDATED_ASSET_ID)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .regularPrice(UPDATED_REGULAR_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .dateOnSaleFrom(UPDATED_DATE_ON_SALE_FROM)
            .dateOnSaleTo(UPDATED_DATE_ON_SALE_TO)
            .isDraft(UPDATED_IS_DRAFT)
            .useParentDetails(UPDATED_USE_PARENT_DETAILS)
            .saleStatus(UPDATED_SALE_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductVariation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductVariation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        ProductVariation testProductVariation = productVariationList.get(productVariationList.size() - 1);
        assertThat(testProductVariation.getAssetId()).isEqualTo(UPDATED_ASSET_ID);
        assertThat(testProductVariation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductVariation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductVariation.getRegularPrice()).isEqualByComparingTo(UPDATED_REGULAR_PRICE);
        assertThat(testProductVariation.getSalePrice()).isEqualByComparingTo(UPDATED_SALE_PRICE);
        assertThat(testProductVariation.getDateOnSaleFrom()).isEqualTo(UPDATED_DATE_ON_SALE_FROM);
        assertThat(testProductVariation.getDateOnSaleTo()).isEqualTo(UPDATED_DATE_ON_SALE_TO);
        assertThat(testProductVariation.getIsDraft()).isEqualTo(UPDATED_IS_DRAFT);
        assertThat(testProductVariation.getUseParentDetails()).isEqualTo(UPDATED_USE_PARENT_DETAILS);
        assertThat(testProductVariation.getSaleStatus()).isEqualTo(UPDATED_SALE_STATUS);
    }

    @Test
    void patchNonExistingProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productVariationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductVariation() throws Exception {
        int databaseSizeBeforeUpdate = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        productVariation.setId(count.incrementAndGet());

        // Create the ProductVariation
        ProductVariationDTO productVariationDTO = productVariationMapper.toDto(productVariation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productVariationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductVariation in the database
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductVariation() {
        // Initialize the database
        productVariationRepository.save(productVariation).block();
        productVariationRepository.save(productVariation).block();
        productVariationSearchRepository.save(productVariation).block();

        int databaseSizeBeforeDelete = productVariationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productVariation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productVariation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductVariation> productVariationList = productVariationRepository.findAll().collectList().block();
        assertThat(productVariationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productVariationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductVariation() {
        // Initialize the database
        productVariation = productVariationRepository.save(productVariation).block();
        productVariationSearchRepository.save(productVariation).block();

        // Search the productVariation
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productVariation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productVariation.getId().intValue()))
            .jsonPath("$.[*].assetId")
            .value(hasItem(DEFAULT_ASSET_ID))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].regularPrice")
            .value(hasItem(sameNumber(DEFAULT_REGULAR_PRICE)))
            .jsonPath("$.[*].salePrice")
            .value(hasItem(sameNumber(DEFAULT_SALE_PRICE)))
            .jsonPath("$.[*].dateOnSaleFrom")
            .value(hasItem(DEFAULT_DATE_ON_SALE_FROM.toString()))
            .jsonPath("$.[*].dateOnSaleTo")
            .value(hasItem(DEFAULT_DATE_ON_SALE_TO.toString()))
            .jsonPath("$.[*].isDraft")
            .value(hasItem(DEFAULT_IS_DRAFT.booleanValue()))
            .jsonPath("$.[*].useParentDetails")
            .value(hasItem(DEFAULT_USE_PARENT_DETAILS.booleanValue()))
            .jsonPath("$.[*].saleStatus")
            .value(hasItem(DEFAULT_SALE_STATUS.toString()));
    }
}
