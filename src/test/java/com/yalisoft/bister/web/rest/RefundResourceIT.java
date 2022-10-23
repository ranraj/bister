package com.yalisoft.bister.web.rest;

import static com.yalisoft.bister.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Refund;
import com.yalisoft.bister.domain.Transaction;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.domain.enumeration.RefundStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.RefundRepository;
import com.yalisoft.bister.repository.search.RefundSearchRepository;
import com.yalisoft.bister.service.RefundService;
import com.yalisoft.bister.service.dto.RefundDTO;
import com.yalisoft.bister.service.mapper.RefundMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link RefundResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RefundResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Long DEFAULT_ORDER_CODE = 1L;
    private static final Long UPDATED_ORDER_CODE = 2L;

    private static final RefundStatus DEFAULT_STATUS = RefundStatus.PENDING;
    private static final RefundStatus UPDATED_STATUS = RefundStatus.COMPLETE;

    private static final String ENTITY_API_URL = "/api/refunds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/refunds";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RefundRepository refundRepository;

    @Mock
    private RefundRepository refundRepositoryMock;

    @Autowired
    private RefundMapper refundMapper;

    @Mock
    private RefundService refundServiceMock;

    @Autowired
    private RefundSearchRepository refundSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Refund refund;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createEntity(EntityManager em) {
        Refund refund = new Refund().amount(DEFAULT_AMOUNT).reason(DEFAULT_REASON).orderCode(DEFAULT_ORDER_CODE).status(DEFAULT_STATUS);
        // Add required entity
        Transaction transaction;
        transaction = em.insert(TransactionResourceIT.createEntity(em)).block();
        refund.setTransaction(transaction);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        refund.setUser(user);
        return refund;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createUpdatedEntity(EntityManager em) {
        Refund refund = new Refund().amount(UPDATED_AMOUNT).reason(UPDATED_REASON).orderCode(UPDATED_ORDER_CODE).status(UPDATED_STATUS);
        // Add required entity
        Transaction transaction;
        transaction = em.insert(TransactionResourceIT.createUpdatedEntity(em)).block();
        refund.setTransaction(transaction);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        refund.setUser(user);
        return refund;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Refund.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        TransactionResourceIT.deleteEntities(em);
        UserResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        refundSearchRepository.deleteAll().block();
        assertThat(refundSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        refund = createEntity(em);
    }

    @Test
    void createRefund() throws Exception {
        int databaseSizeBeforeCreate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Refund testRefund = refundList.get(refundList.size() - 1);
        assertThat(testRefund.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testRefund.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testRefund.getOrderCode()).isEqualTo(DEFAULT_ORDER_CODE);
        assertThat(testRefund.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createRefundWithExistingId() throws Exception {
        // Create the Refund with an existing ID
        refund.setId(1L);
        RefundDTO refundDTO = refundMapper.toDto(refund);

        int databaseSizeBeforeCreate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        // set the field null
        refund.setAmount(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReasonIsRequired() throws Exception {
        int databaseSizeBeforeTest = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        // set the field null
        refund.setReason(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        // set the field null
        refund.setOrderCode(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        // set the field null
        refund.setStatus(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRefunds() {
        // Initialize the database
        refundRepository.save(refund).block();

        // Get all the refundList
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
            .value(hasItem(refund.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON))
            .jsonPath("$.[*].orderCode")
            .value(hasItem(DEFAULT_ORDER_CODE.intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRefundsWithEagerRelationshipsIsEnabled() {
        when(refundServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(refundServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRefundsWithEagerRelationshipsIsNotEnabled() {
        when(refundServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(refundRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRefund() {
        // Initialize the database
        refundRepository.save(refund).block();

        // Get the refund
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, refund.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(refund.getId().intValue()))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON))
            .jsonPath("$.orderCode")
            .value(is(DEFAULT_ORDER_CODE.intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingRefund() {
        // Get the refund
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRefund() throws Exception {
        // Initialize the database
        refundRepository.save(refund).block();

        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        refundSearchRepository.save(refund).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());

        // Update the refund
        Refund updatedRefund = refundRepository.findById(refund.getId()).block();
        updatedRefund.amount(UPDATED_AMOUNT).reason(UPDATED_REASON).orderCode(UPDATED_ORDER_CODE).status(UPDATED_STATUS);
        RefundDTO refundDTO = refundMapper.toDto(updatedRefund);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, refundDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        Refund testRefund = refundList.get(refundList.size() - 1);
        assertThat(testRefund.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRefund.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testRefund.getOrderCode()).isEqualTo(UPDATED_ORDER_CODE);
        assertThat(testRefund.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Refund> refundSearchList = IterableUtils.toList(refundSearchRepository.findAll().collectList().block());
                Refund testRefundSearch = refundSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRefundSearch.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
                assertThat(testRefundSearch.getReason()).isEqualTo(UPDATED_REASON);
                assertThat(testRefundSearch.getOrderCode()).isEqualTo(UPDATED_ORDER_CODE);
                assertThat(testRefundSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    void putNonExistingRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, refundDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        refundRepository.save(refund).block();

        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund.amount(UPDATED_AMOUNT).reason(UPDATED_REASON).orderCode(UPDATED_ORDER_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRefund))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        Refund testRefund = refundList.get(refundList.size() - 1);
        assertThat(testRefund.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRefund.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testRefund.getOrderCode()).isEqualTo(UPDATED_ORDER_CODE);
        assertThat(testRefund.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        refundRepository.save(refund).block();

        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund.amount(UPDATED_AMOUNT).reason(UPDATED_REASON).orderCode(UPDATED_ORDER_CODE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRefund))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        Refund testRefund = refundList.get(refundList.size() - 1);
        assertThat(testRefund.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testRefund.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testRefund.getOrderCode()).isEqualTo(UPDATED_ORDER_CODE);
        assertThat(testRefund.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, refundDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRefund() throws Exception {
        int databaseSizeBeforeUpdate = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        refund.setId(count.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Refund in the database
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRefund() {
        // Initialize the database
        refundRepository.save(refund).block();
        refundRepository.save(refund).block();
        refundSearchRepository.save(refund).block();

        int databaseSizeBeforeDelete = refundRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the refund
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, refund.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Refund> refundList = refundRepository.findAll().collectList().block();
        assertThat(refundList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRefund() {
        // Initialize the database
        refund = refundRepository.save(refund).block();
        refundSearchRepository.save(refund).block();

        // Search the refund
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + refund.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(refund.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON))
            .jsonPath("$.[*].orderCode")
            .value(hasItem(DEFAULT_ORDER_CODE.intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }
}
