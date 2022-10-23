package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Promotion;
import com.yalisoft.bister.domain.enumeration.PromotionContentType;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.PromotionRepository;
import com.yalisoft.bister.repository.search.PromotionSearchRepository;
import com.yalisoft.bister.service.dto.PromotionDTO;
import com.yalisoft.bister.service.mapper.PromotionMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PromotionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PromotionResourceIT {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final PromotionContentType DEFAULT_CONTENT_TYPE = PromotionContentType.TEMPLATE;
    private static final PromotionContentType UPDATED_CONTENT_TYPE = PromotionContentType.ATTACHMENT;

    private static final String DEFAULT_RECIPIENTS = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENTS = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPIENT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT_GROUP = "BBBBBBBBBB";

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SEND_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SEND_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_ATTACHMENT_ID = 1L;
    private static final Long UPDATED_ATTACHMENT_ID = 2L;

    private static final String ENTITY_API_URL = "/api/promotions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/promotions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private PromotionSearchRepository promotionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Promotion promotion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createEntity(EntityManager em) {
        Promotion promotion = new Promotion()
            .productId(DEFAULT_PRODUCT_ID)
            .projectId(DEFAULT_PROJECT_ID)
            .contentType(DEFAULT_CONTENT_TYPE)
            .recipients(DEFAULT_RECIPIENTS)
            .recipientGroup(DEFAULT_RECIPIENT_GROUP)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .sendAt(DEFAULT_SEND_AT)
            .attachmentId(DEFAULT_ATTACHMENT_ID);
        return promotion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createUpdatedEntity(EntityManager em) {
        Promotion promotion = new Promotion()
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .contentType(UPDATED_CONTENT_TYPE)
            .recipients(UPDATED_RECIPIENTS)
            .recipientGroup(UPDATED_RECIPIENT_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .sendAt(UPDATED_SEND_AT)
            .attachmentId(UPDATED_ATTACHMENT_ID);
        return promotion;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Promotion.class).block();
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
        promotionSearchRepository.deleteAll().block();
        assertThat(promotionSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        promotion = createEntity(em);
    }

    @Test
    void createPromotion() throws Exception {
        int databaseSizeBeforeCreate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Promotion testPromotion = promotionList.get(promotionList.size() - 1);
        assertThat(testPromotion.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testPromotion.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testPromotion.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testPromotion.getRecipients()).isEqualTo(DEFAULT_RECIPIENTS);
        assertThat(testPromotion.getRecipientGroup()).isEqualTo(DEFAULT_RECIPIENT_GROUP);
        assertThat(testPromotion.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPromotion.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPromotion.getSendAt()).isEqualTo(DEFAULT_SEND_AT);
        assertThat(testPromotion.getAttachmentId()).isEqualTo(DEFAULT_ATTACHMENT_ID);
    }

    @Test
    void createPromotionWithExistingId() throws Exception {
        // Create the Promotion with an existing ID
        promotion.setId(1L);
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        int databaseSizeBeforeCreate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkContentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        // set the field null
        promotion.setContentType(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        // set the field null
        promotion.setCreatedAt(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSendAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        // set the field null
        promotion.setSendAt(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPromotions() {
        // Initialize the database
        promotionRepository.save(promotion).block();

        // Get all the promotionList
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
            .value(hasItem(promotion.getId().intValue()))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE.toString()))
            .jsonPath("$.[*].recipients")
            .value(hasItem(DEFAULT_RECIPIENTS))
            .jsonPath("$.[*].recipientGroup")
            .value(hasItem(DEFAULT_RECIPIENT_GROUP))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].sendAt")
            .value(hasItem(DEFAULT_SEND_AT.toString()))
            .jsonPath("$.[*].attachmentId")
            .value(hasItem(DEFAULT_ATTACHMENT_ID.intValue()));
    }

    @Test
    void getPromotion() {
        // Initialize the database
        promotionRepository.save(promotion).block();

        // Get the promotion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, promotion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(promotion.getId().intValue()))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.projectId")
            .value(is(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.contentType")
            .value(is(DEFAULT_CONTENT_TYPE.toString()))
            .jsonPath("$.recipients")
            .value(is(DEFAULT_RECIPIENTS))
            .jsonPath("$.recipientGroup")
            .value(is(DEFAULT_RECIPIENT_GROUP))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.sendAt")
            .value(is(DEFAULT_SEND_AT.toString()))
            .jsonPath("$.attachmentId")
            .value(is(DEFAULT_ATTACHMENT_ID.intValue()));
    }

    @Test
    void getNonExistingPromotion() {
        // Get the promotion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPromotion() throws Exception {
        // Initialize the database
        promotionRepository.save(promotion).block();

        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        promotionSearchRepository.save(promotion).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());

        // Update the promotion
        Promotion updatedPromotion = promotionRepository.findById(promotion.getId()).block();
        updatedPromotion
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .contentType(UPDATED_CONTENT_TYPE)
            .recipients(UPDATED_RECIPIENTS)
            .recipientGroup(UPDATED_RECIPIENT_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .sendAt(UPDATED_SEND_AT)
            .attachmentId(UPDATED_ATTACHMENT_ID);
        PromotionDTO promotionDTO = promotionMapper.toDto(updatedPromotion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, promotionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        Promotion testPromotion = promotionList.get(promotionList.size() - 1);
        assertThat(testPromotion.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testPromotion.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testPromotion.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testPromotion.getRecipients()).isEqualTo(UPDATED_RECIPIENTS);
        assertThat(testPromotion.getRecipientGroup()).isEqualTo(UPDATED_RECIPIENT_GROUP);
        assertThat(testPromotion.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPromotion.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPromotion.getSendAt()).isEqualTo(UPDATED_SEND_AT);
        assertThat(testPromotion.getAttachmentId()).isEqualTo(UPDATED_ATTACHMENT_ID);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Promotion> promotionSearchList = IterableUtils.toList(promotionSearchRepository.findAll().collectList().block());
                Promotion testPromotionSearch = promotionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPromotionSearch.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
                assertThat(testPromotionSearch.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
                assertThat(testPromotionSearch.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
                assertThat(testPromotionSearch.getRecipients()).isEqualTo(UPDATED_RECIPIENTS);
                assertThat(testPromotionSearch.getRecipientGroup()).isEqualTo(UPDATED_RECIPIENT_GROUP);
                assertThat(testPromotionSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
                assertThat(testPromotionSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testPromotionSearch.getSendAt()).isEqualTo(UPDATED_SEND_AT);
                assertThat(testPromotionSearch.getAttachmentId()).isEqualTo(UPDATED_ATTACHMENT_ID);
            });
    }

    @Test
    void putNonExistingPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, promotionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        promotionRepository.save(promotion).block();

        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .projectId(UPDATED_PROJECT_ID)
            .contentType(UPDATED_CONTENT_TYPE)
            .recipients(UPDATED_RECIPIENTS)
            .recipientGroup(UPDATED_RECIPIENT_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .sendAt(UPDATED_SEND_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPromotion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        Promotion testPromotion = promotionList.get(promotionList.size() - 1);
        assertThat(testPromotion.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testPromotion.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testPromotion.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testPromotion.getRecipients()).isEqualTo(UPDATED_RECIPIENTS);
        assertThat(testPromotion.getRecipientGroup()).isEqualTo(UPDATED_RECIPIENT_GROUP);
        assertThat(testPromotion.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPromotion.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPromotion.getSendAt()).isEqualTo(UPDATED_SEND_AT);
        assertThat(testPromotion.getAttachmentId()).isEqualTo(DEFAULT_ATTACHMENT_ID);
    }

    @Test
    void fullUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        promotionRepository.save(promotion).block();

        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .contentType(UPDATED_CONTENT_TYPE)
            .recipients(UPDATED_RECIPIENTS)
            .recipientGroup(UPDATED_RECIPIENT_GROUP)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .sendAt(UPDATED_SEND_AT)
            .attachmentId(UPDATED_ATTACHMENT_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPromotion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        Promotion testPromotion = promotionList.get(promotionList.size() - 1);
        assertThat(testPromotion.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testPromotion.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testPromotion.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testPromotion.getRecipients()).isEqualTo(UPDATED_RECIPIENTS);
        assertThat(testPromotion.getRecipientGroup()).isEqualTo(UPDATED_RECIPIENT_GROUP);
        assertThat(testPromotion.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPromotion.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPromotion.getSendAt()).isEqualTo(UPDATED_SEND_AT);
        assertThat(testPromotion.getAttachmentId()).isEqualTo(UPDATED_ATTACHMENT_ID);
    }

    @Test
    void patchNonExistingPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, promotionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPromotion() throws Exception {
        int databaseSizeBeforeUpdate = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        promotion.setId(count.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(promotionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Promotion in the database
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePromotion() {
        // Initialize the database
        promotionRepository.save(promotion).block();
        promotionRepository.save(promotion).block();
        promotionSearchRepository.save(promotion).block();

        int databaseSizeBeforeDelete = promotionRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the promotion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, promotion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Promotion> promotionList = promotionRepository.findAll().collectList().block();
        assertThat(promotionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPromotion() {
        // Initialize the database
        promotion = promotionRepository.save(promotion).block();
        promotionSearchRepository.save(promotion).block();

        // Search the promotion
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + promotion.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(promotion.getId().intValue()))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE.toString()))
            .jsonPath("$.[*].recipients")
            .value(hasItem(DEFAULT_RECIPIENTS))
            .jsonPath("$.[*].recipientGroup")
            .value(hasItem(DEFAULT_RECIPIENT_GROUP))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].sendAt")
            .value(hasItem(DEFAULT_SEND_AT.toString()))
            .jsonPath("$.[*].attachmentId")
            .value(hasItem(DEFAULT_ATTACHMENT_ID.intValue()));
    }
}
