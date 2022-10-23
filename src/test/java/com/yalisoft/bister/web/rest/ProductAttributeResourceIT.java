package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.ProductAttribute;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductAttributeRepository;
import com.yalisoft.bister.repository.search.ProductAttributeSearchRepository;
import com.yalisoft.bister.service.dto.ProductAttributeDTO;
import com.yalisoft.bister.service.mapper.ProductAttributeMapper;
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
 * Integration tests for the {@link ProductAttributeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductAttributeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    private static final String ENTITY_API_URL = "/api/product-attributes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-attributes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private ProductAttributeSearchRepository productAttributeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductAttribute productAttribute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAttribute createEntity(EntityManager em) {
        ProductAttribute productAttribute = new ProductAttribute()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .type(DEFAULT_TYPE)
            .notes(DEFAULT_NOTES)
            .visible(DEFAULT_VISIBLE);
        return productAttribute;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAttribute createUpdatedEntity(EntityManager em) {
        ProductAttribute productAttribute = new ProductAttribute()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .type(UPDATED_TYPE)
            .notes(UPDATED_NOTES)
            .visible(UPDATED_VISIBLE);
        return productAttribute;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductAttribute.class).block();
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
        productAttributeSearchRepository.deleteAll().block();
        assertThat(productAttributeSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productAttribute = createEntity(em);
    }

    @Test
    void createProductAttribute() throws Exception {
        int databaseSizeBeforeCreate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductAttribute testProductAttribute = productAttributeList.get(productAttributeList.size() - 1);
        assertThat(testProductAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductAttribute.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductAttribute.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testProductAttribute.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testProductAttribute.getVisible()).isEqualTo(DEFAULT_VISIBLE);
    }

    @Test
    void createProductAttributeWithExistingId() throws Exception {
        // Create the ProductAttribute with an existing ID
        productAttribute.setId(1L);
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        int databaseSizeBeforeCreate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        // set the field null
        productAttribute.setName(null);

        // Create the ProductAttribute, which fails.
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        // set the field null
        productAttribute.setSlug(null);

        // Create the ProductAttribute, which fails.
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        // set the field null
        productAttribute.setType(null);

        // Create the ProductAttribute, which fails.
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotesIsRequired() throws Exception {
        int databaseSizeBeforeTest = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        // set the field null
        productAttribute.setNotes(null);

        // Create the ProductAttribute, which fails.
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductAttributes() {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();

        // Get all the productAttributeList
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
            .value(hasItem(productAttribute.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES))
            .jsonPath("$.[*].visible")
            .value(hasItem(DEFAULT_VISIBLE.booleanValue()));
    }

    @Test
    void getProductAttribute() {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();

        // Get the productAttribute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productAttribute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productAttribute.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES))
            .jsonPath("$.visible")
            .value(is(DEFAULT_VISIBLE.booleanValue()));
    }

    @Test
    void getNonExistingProductAttribute() {
        // Get the productAttribute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductAttribute() throws Exception {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();

        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        productAttributeSearchRepository.save(productAttribute).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());

        // Update the productAttribute
        ProductAttribute updatedProductAttribute = productAttributeRepository.findById(productAttribute.getId()).block();
        updatedProductAttribute.name(UPDATED_NAME).slug(UPDATED_SLUG).type(UPDATED_TYPE).notes(UPDATED_NOTES).visible(UPDATED_VISIBLE);
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(updatedProductAttribute);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productAttributeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        ProductAttribute testProductAttribute = productAttributeList.get(productAttributeList.size() - 1);
        assertThat(testProductAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductAttribute.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductAttribute.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProductAttribute.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testProductAttribute.getVisible()).isEqualTo(UPDATED_VISIBLE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductAttribute> productAttributeSearchList = IterableUtils.toList(
                    productAttributeSearchRepository.findAll().collectList().block()
                );
                ProductAttribute testProductAttributeSearch = productAttributeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductAttributeSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProductAttributeSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testProductAttributeSearch.getType()).isEqualTo(UPDATED_TYPE);
                assertThat(testProductAttributeSearch.getNotes()).isEqualTo(UPDATED_NOTES);
                assertThat(testProductAttributeSearch.getVisible()).isEqualTo(UPDATED_VISIBLE);
            });
    }

    @Test
    void putNonExistingProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productAttributeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductAttributeWithPatch() throws Exception {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();

        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();

        // Update the productAttribute using partial update
        ProductAttribute partialUpdatedProductAttribute = new ProductAttribute();
        partialUpdatedProductAttribute.setId(productAttribute.getId());

        partialUpdatedProductAttribute.slug(UPDATED_SLUG).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductAttribute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductAttribute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        ProductAttribute testProductAttribute = productAttributeList.get(productAttributeList.size() - 1);
        assertThat(testProductAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductAttribute.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductAttribute.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProductAttribute.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testProductAttribute.getVisible()).isEqualTo(DEFAULT_VISIBLE);
    }

    @Test
    void fullUpdateProductAttributeWithPatch() throws Exception {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();

        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();

        // Update the productAttribute using partial update
        ProductAttribute partialUpdatedProductAttribute = new ProductAttribute();
        partialUpdatedProductAttribute.setId(productAttribute.getId());

        partialUpdatedProductAttribute
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .type(UPDATED_TYPE)
            .notes(UPDATED_NOTES)
            .visible(UPDATED_VISIBLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductAttribute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductAttribute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        ProductAttribute testProductAttribute = productAttributeList.get(productAttributeList.size() - 1);
        assertThat(testProductAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductAttribute.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductAttribute.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProductAttribute.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testProductAttribute.getVisible()).isEqualTo(UPDATED_VISIBLE);
    }

    @Test
    void patchNonExistingProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productAttributeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductAttribute() throws Exception {
        int databaseSizeBeforeUpdate = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        productAttribute.setId(count.incrementAndGet());

        // Create the ProductAttribute
        ProductAttributeDTO productAttributeDTO = productAttributeMapper.toDto(productAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productAttributeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductAttribute in the database
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductAttribute() {
        // Initialize the database
        productAttributeRepository.save(productAttribute).block();
        productAttributeRepository.save(productAttribute).block();
        productAttributeSearchRepository.save(productAttribute).block();

        int databaseSizeBeforeDelete = productAttributeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productAttribute
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productAttribute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductAttribute> productAttributeList = productAttributeRepository.findAll().collectList().block();
        assertThat(productAttributeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productAttributeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductAttribute() {
        // Initialize the database
        productAttribute = productAttributeRepository.save(productAttribute).block();
        productAttributeSearchRepository.save(productAttribute).block();

        // Search the productAttribute
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productAttribute.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productAttribute.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES))
            .jsonPath("$.[*].visible")
            .value(hasItem(DEFAULT_VISIBLE.booleanValue()));
    }
}
