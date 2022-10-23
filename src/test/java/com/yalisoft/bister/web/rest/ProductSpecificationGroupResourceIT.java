package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.ProductSpecificationGroup;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProductSpecificationGroupRepository;
import com.yalisoft.bister.repository.search.ProductSpecificationGroupSearchRepository;
import com.yalisoft.bister.service.ProductSpecificationGroupService;
import com.yalisoft.bister.service.dto.ProductSpecificationGroupDTO;
import com.yalisoft.bister.service.mapper.ProductSpecificationGroupMapper;
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
 * Integration tests for the {@link ProductSpecificationGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductSpecificationGroupResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-specification-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-specification-groups";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductSpecificationGroupRepository productSpecificationGroupRepository;

    @Mock
    private ProductSpecificationGroupRepository productSpecificationGroupRepositoryMock;

    @Autowired
    private ProductSpecificationGroupMapper productSpecificationGroupMapper;

    @Mock
    private ProductSpecificationGroupService productSpecificationGroupServiceMock;

    @Autowired
    private ProductSpecificationGroupSearchRepository productSpecificationGroupSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductSpecificationGroup productSpecificationGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecificationGroup createEntity(EntityManager em) {
        ProductSpecificationGroup productSpecificationGroup = new ProductSpecificationGroup()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION);
        return productSpecificationGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecificationGroup createUpdatedEntity(EntityManager em) {
        ProductSpecificationGroup productSpecificationGroup = new ProductSpecificationGroup()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION);
        return productSpecificationGroup;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductSpecificationGroup.class).block();
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
        productSpecificationGroupSearchRepository.deleteAll().block();
        assertThat(productSpecificationGroupSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productSpecificationGroup = createEntity(em);
    }

    @Test
    void createProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeCreate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    productSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductSpecificationGroup testProductSpecificationGroup = productSpecificationGroupList.get(
            productSpecificationGroupList.size() - 1
        );
        assertThat(testProductSpecificationGroup.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProductSpecificationGroup.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductSpecificationGroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createProductSpecificationGroupWithExistingId() throws Exception {
        // Create the ProductSpecificationGroup with an existing ID
        productSpecificationGroup.setId(1L);
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        int databaseSizeBeforeCreate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        // set the field null
        productSpecificationGroup.setTitle(null);

        // Create the ProductSpecificationGroup, which fails.
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        // set the field null
        productSpecificationGroup.setSlug(null);

        // Create the ProductSpecificationGroup, which fails.
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductSpecificationGroups() {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();

        // Get all the productSpecificationGroupList
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
            .value(hasItem(productSpecificationGroup.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductSpecificationGroupsWithEagerRelationshipsIsEnabled() {
        when(productSpecificationGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productSpecificationGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductSpecificationGroupsWithEagerRelationshipsIsNotEnabled() {
        when(productSpecificationGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productSpecificationGroupRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProductSpecificationGroup() {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();

        // Get the productSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productSpecificationGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productSpecificationGroup.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingProductSpecificationGroup() {
        // Get the productSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductSpecificationGroup() throws Exception {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();

        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        productSpecificationGroupSearchRepository.save(productSpecificationGroup).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());

        // Update the productSpecificationGroup
        ProductSpecificationGroup updatedProductSpecificationGroup = productSpecificationGroupRepository
            .findById(productSpecificationGroup.getId())
            .block();
        updatedProductSpecificationGroup.title(UPDATED_TITLE).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION);
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(updatedProductSpecificationGroup);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productSpecificationGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecificationGroup testProductSpecificationGroup = productSpecificationGroupList.get(
            productSpecificationGroupList.size() - 1
        );
        assertThat(testProductSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductSpecificationGroup.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductSpecificationGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    productSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductSpecificationGroup> productSpecificationGroupSearchList = IterableUtils.toList(
                    productSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                ProductSpecificationGroup testProductSpecificationGroupSearch = productSpecificationGroupSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testProductSpecificationGroupSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProductSpecificationGroupSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testProductSpecificationGroupSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productSpecificationGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductSpecificationGroupWithPatch() throws Exception {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();

        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();

        // Update the productSpecificationGroup using partial update
        ProductSpecificationGroup partialUpdatedProductSpecificationGroup = new ProductSpecificationGroup();
        partialUpdatedProductSpecificationGroup.setId(productSpecificationGroup.getId());

        partialUpdatedProductSpecificationGroup.title(UPDATED_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductSpecificationGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecificationGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecificationGroup testProductSpecificationGroup = productSpecificationGroupList.get(
            productSpecificationGroupList.size() - 1
        );
        assertThat(testProductSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductSpecificationGroup.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProductSpecificationGroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateProductSpecificationGroupWithPatch() throws Exception {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();

        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();

        // Update the productSpecificationGroup using partial update
        ProductSpecificationGroup partialUpdatedProductSpecificationGroup = new ProductSpecificationGroup();
        partialUpdatedProductSpecificationGroup.setId(productSpecificationGroup.getId());

        partialUpdatedProductSpecificationGroup.title(UPDATED_TITLE).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductSpecificationGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecificationGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecificationGroup testProductSpecificationGroup = productSpecificationGroupList.get(
            productSpecificationGroupList.size() - 1
        );
        assertThat(testProductSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProductSpecificationGroup.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProductSpecificationGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productSpecificationGroupDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        productSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProductSpecificationGroup
        ProductSpecificationGroupDTO productSpecificationGroupDTO = productSpecificationGroupMapper.toDto(productSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductSpecificationGroup in the database
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductSpecificationGroup() {
        // Initialize the database
        productSpecificationGroupRepository.save(productSpecificationGroup).block();
        productSpecificationGroupRepository.save(productSpecificationGroup).block();
        productSpecificationGroupSearchRepository.save(productSpecificationGroup).block();

        int databaseSizeBeforeDelete = productSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productSpecificationGroup
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productSpecificationGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductSpecificationGroup> productSpecificationGroupList = productSpecificationGroupRepository.findAll().collectList().block();
        assertThat(productSpecificationGroupList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductSpecificationGroup() {
        // Initialize the database
        productSpecificationGroup = productSpecificationGroupRepository.save(productSpecificationGroup).block();
        productSpecificationGroupSearchRepository.save(productSpecificationGroup).block();

        // Search the productSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productSpecificationGroup.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productSpecificationGroup.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
