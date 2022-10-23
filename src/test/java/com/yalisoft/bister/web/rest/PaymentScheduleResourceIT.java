package com.yalisoft.bister.web.rest;

import static com.yalisoft.bister.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.domain.enumeration.PaymentScheduleStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.PaymentScheduleRepository;
import com.yalisoft.bister.repository.search.PaymentScheduleSearchRepository;
import com.yalisoft.bister.service.dto.PaymentScheduleDTO;
import com.yalisoft.bister.service.mapper.PaymentScheduleMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link PaymentScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentScheduleResourceIT {

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);

    private static final String DEFAULT_REMARKS = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBBBBBBBBBBBB";

    private static final PaymentScheduleStatus DEFAULT_STATUS = PaymentScheduleStatus.PAID;
    private static final PaymentScheduleStatus UPDATED_STATUS = PaymentScheduleStatus.PENDING;

    private static final Boolean DEFAULT_IS_OVER_DUE = false;
    private static final Boolean UPDATED_IS_OVER_DUE = true;

    private static final String ENTITY_API_URL = "/api/payment-schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/payment-schedules";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private PaymentScheduleMapper paymentScheduleMapper;

    @Autowired
    private PaymentScheduleSearchRepository paymentScheduleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PaymentSchedule paymentSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentSchedule createEntity(EntityManager em) {
        PaymentSchedule paymentSchedule = new PaymentSchedule()
            .dueDate(DEFAULT_DUE_DATE)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .remarks(DEFAULT_REMARKS)
            .status(DEFAULT_STATUS)
            .isOverDue(DEFAULT_IS_OVER_DUE);
        return paymentSchedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentSchedule createUpdatedEntity(EntityManager em) {
        PaymentSchedule paymentSchedule = new PaymentSchedule()
            .dueDate(UPDATED_DUE_DATE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .remarks(UPDATED_REMARKS)
            .status(UPDATED_STATUS)
            .isOverDue(UPDATED_IS_OVER_DUE);
        return paymentSchedule;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PaymentSchedule.class).block();
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
        paymentScheduleSearchRepository.deleteAll().block();
        assertThat(paymentScheduleSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        paymentSchedule = createEntity(em);
    }

    @Test
    void createPaymentSchedule() throws Exception {
        int databaseSizeBeforeCreate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        PaymentSchedule testPaymentSchedule = paymentScheduleList.get(paymentScheduleList.size() - 1);
        assertThat(testPaymentSchedule.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testPaymentSchedule.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
        assertThat(testPaymentSchedule.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testPaymentSchedule.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPaymentSchedule.getIsOverDue()).isEqualTo(DEFAULT_IS_OVER_DUE);
    }

    @Test
    void createPaymentScheduleWithExistingId() throws Exception {
        // Create the PaymentSchedule with an existing ID
        paymentSchedule.setId(1L);
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        int databaseSizeBeforeCreate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        // set the field null
        paymentSchedule.setDueDate(null);

        // Create the PaymentSchedule, which fails.
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        // set the field null
        paymentSchedule.setTotalPrice(null);

        // Create the PaymentSchedule, which fails.
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        // set the field null
        paymentSchedule.setStatus(null);

        // Create the PaymentSchedule, which fails.
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPaymentSchedules() {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();

        // Get all the paymentScheduleList
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
            .value(hasItem(paymentSchedule.getId().intValue()))
            .jsonPath("$.[*].dueDate")
            .value(hasItem(DEFAULT_DUE_DATE.toString()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].isOverDue")
            .value(hasItem(DEFAULT_IS_OVER_DUE.booleanValue()));
    }

    @Test
    void getPaymentSchedule() {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();

        // Get the paymentSchedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, paymentSchedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(paymentSchedule.getId().intValue()))
            .jsonPath("$.dueDate")
            .value(is(DEFAULT_DUE_DATE.toString()))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.isOverDue")
            .value(is(DEFAULT_IS_OVER_DUE.booleanValue()));
    }

    @Test
    void getNonExistingPaymentSchedule() {
        // Get the paymentSchedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPaymentSchedule() throws Exception {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();

        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        paymentScheduleSearchRepository.save(paymentSchedule).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());

        // Update the paymentSchedule
        PaymentSchedule updatedPaymentSchedule = paymentScheduleRepository.findById(paymentSchedule.getId()).block();
        updatedPaymentSchedule
            .dueDate(UPDATED_DUE_DATE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .remarks(UPDATED_REMARKS)
            .status(UPDATED_STATUS)
            .isOverDue(UPDATED_IS_OVER_DUE);
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(updatedPaymentSchedule);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentScheduleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        PaymentSchedule testPaymentSchedule = paymentScheduleList.get(paymentScheduleList.size() - 1);
        assertThat(testPaymentSchedule.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testPaymentSchedule.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
        assertThat(testPaymentSchedule.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testPaymentSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPaymentSchedule.getIsOverDue()).isEqualTo(UPDATED_IS_OVER_DUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PaymentSchedule> paymentScheduleSearchList = IterableUtils.toList(
                    paymentScheduleSearchRepository.findAll().collectList().block()
                );
                PaymentSchedule testPaymentScheduleSearch = paymentScheduleSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPaymentScheduleSearch.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
                assertThat(testPaymentScheduleSearch.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
                assertThat(testPaymentScheduleSearch.getRemarks()).isEqualTo(UPDATED_REMARKS);
                assertThat(testPaymentScheduleSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testPaymentScheduleSearch.getIsOverDue()).isEqualTo(UPDATED_IS_OVER_DUE);
            });
    }

    @Test
    void putNonExistingPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentScheduleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePaymentScheduleWithPatch() throws Exception {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();

        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();

        // Update the paymentSchedule using partial update
        PaymentSchedule partialUpdatedPaymentSchedule = new PaymentSchedule();
        partialUpdatedPaymentSchedule.setId(paymentSchedule.getId());

        partialUpdatedPaymentSchedule.totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS).isOverDue(UPDATED_IS_OVER_DUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        PaymentSchedule testPaymentSchedule = paymentScheduleList.get(paymentScheduleList.size() - 1);
        assertThat(testPaymentSchedule.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testPaymentSchedule.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
        assertThat(testPaymentSchedule.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testPaymentSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPaymentSchedule.getIsOverDue()).isEqualTo(UPDATED_IS_OVER_DUE);
    }

    @Test
    void fullUpdatePaymentScheduleWithPatch() throws Exception {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();

        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();

        // Update the paymentSchedule using partial update
        PaymentSchedule partialUpdatedPaymentSchedule = new PaymentSchedule();
        partialUpdatedPaymentSchedule.setId(paymentSchedule.getId());

        partialUpdatedPaymentSchedule
            .dueDate(UPDATED_DUE_DATE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .remarks(UPDATED_REMARKS)
            .status(UPDATED_STATUS)
            .isOverDue(UPDATED_IS_OVER_DUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        PaymentSchedule testPaymentSchedule = paymentScheduleList.get(paymentScheduleList.size() - 1);
        assertThat(testPaymentSchedule.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testPaymentSchedule.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
        assertThat(testPaymentSchedule.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testPaymentSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPaymentSchedule.getIsOverDue()).isEqualTo(UPDATED_IS_OVER_DUE);
    }

    @Test
    void patchNonExistingPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentScheduleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPaymentSchedule() throws Exception {
        int databaseSizeBeforeUpdate = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        paymentSchedule.setId(count.incrementAndGet());

        // Create the PaymentSchedule
        PaymentScheduleDTO paymentScheduleDTO = paymentScheduleMapper.toDto(paymentSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentScheduleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentSchedule in the database
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePaymentSchedule() {
        // Initialize the database
        paymentScheduleRepository.save(paymentSchedule).block();
        paymentScheduleRepository.save(paymentSchedule).block();
        paymentScheduleSearchRepository.save(paymentSchedule).block();

        int databaseSizeBeforeDelete = paymentScheduleRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the paymentSchedule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, paymentSchedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PaymentSchedule> paymentScheduleList = paymentScheduleRepository.findAll().collectList().block();
        assertThat(paymentScheduleList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentScheduleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPaymentSchedule() {
        // Initialize the database
        paymentSchedule = paymentScheduleRepository.save(paymentSchedule).block();
        paymentScheduleSearchRepository.save(paymentSchedule).block();

        // Search the paymentSchedule
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + paymentSchedule.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(paymentSchedule.getId().intValue()))
            .jsonPath("$.[*].dueDate")
            .value(hasItem(DEFAULT_DUE_DATE.toString()))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].isOverDue")
            .value(hasItem(DEFAULT_IS_OVER_DUE.booleanValue()));
    }
}
