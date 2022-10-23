package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.BookingOrder;
import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.domain.enumeration.BookingOrderStatus;
import com.yalisoft.bister.repository.BookingOrderRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.BookingOrderSearchRepository;
import com.yalisoft.bister.service.BookingOrderService;
import com.yalisoft.bister.service.dto.BookingOrderDTO;
import com.yalisoft.bister.service.mapper.BookingOrderMapper;
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
 * Integration tests for the {@link BookingOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BookingOrderResourceIT {

    private static final Instant DEFAULT_PLACED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BookingOrderStatus DEFAULT_STATUS = BookingOrderStatus.BLOCKED;
    private static final BookingOrderStatus UPDATED_STATUS = BookingOrderStatus.BOOKED;

    private static final String DEFAULT_CODE = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBBBBBBBBBBBB";

    private static final Instant DEFAULT_BOOKING_EXPIERY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOOKING_EXPIERY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/booking-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/booking-orders";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Mock
    private BookingOrderRepository bookingOrderRepositoryMock;

    @Autowired
    private BookingOrderMapper bookingOrderMapper;

    @Mock
    private BookingOrderService bookingOrderServiceMock;

    @Autowired
    private BookingOrderSearchRepository bookingOrderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BookingOrder bookingOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingOrder createEntity(EntityManager em) {
        BookingOrder bookingOrder = new BookingOrder()
            .placedDate(DEFAULT_PLACED_DATE)
            .status(DEFAULT_STATUS)
            .code(DEFAULT_CODE)
            .bookingExpieryDate(DEFAULT_BOOKING_EXPIERY_DATE);
        // Add required entity
        ProductVariation productVariation;
        productVariation = em.insert(ProductVariationResourceIT.createEntity(em)).block();
        bookingOrder.setProductVariation(productVariation);
        return bookingOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingOrder createUpdatedEntity(EntityManager em) {
        BookingOrder bookingOrder = new BookingOrder()
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .bookingExpieryDate(UPDATED_BOOKING_EXPIERY_DATE);
        // Add required entity
        ProductVariation productVariation;
        productVariation = em.insert(ProductVariationResourceIT.createUpdatedEntity(em)).block();
        bookingOrder.setProductVariation(productVariation);
        return bookingOrder;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BookingOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductVariationResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        bookingOrderSearchRepository.deleteAll().block();
        assertThat(bookingOrderSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        bookingOrder = createEntity(em);
    }

    @Test
    void createBookingOrder() throws Exception {
        int databaseSizeBeforeCreate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BookingOrder testBookingOrder = bookingOrderList.get(bookingOrderList.size() - 1);
        assertThat(testBookingOrder.getPlacedDate()).isEqualTo(DEFAULT_PLACED_DATE);
        assertThat(testBookingOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBookingOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testBookingOrder.getBookingExpieryDate()).isEqualTo(DEFAULT_BOOKING_EXPIERY_DATE);
    }

    @Test
    void createBookingOrderWithExistingId() throws Exception {
        // Create the BookingOrder with an existing ID
        bookingOrder.setId(1L);
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        int databaseSizeBeforeCreate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPlacedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        // set the field null
        bookingOrder.setPlacedDate(null);

        // Create the BookingOrder, which fails.
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        // set the field null
        bookingOrder.setStatus(null);

        // Create the BookingOrder, which fails.
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBookingOrders() {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();

        // Get all the bookingOrderList
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
            .value(hasItem(bookingOrder.getId().intValue()))
            .jsonPath("$.[*].placedDate")
            .value(hasItem(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].bookingExpieryDate")
            .value(hasItem(DEFAULT_BOOKING_EXPIERY_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingOrdersWithEagerRelationshipsIsEnabled() {
        when(bookingOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(bookingOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingOrdersWithEagerRelationshipsIsNotEnabled() {
        when(bookingOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(bookingOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getBookingOrder() {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();

        // Get the bookingOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bookingOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bookingOrder.getId().intValue()))
            .jsonPath("$.placedDate")
            .value(is(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.bookingExpieryDate")
            .value(is(DEFAULT_BOOKING_EXPIERY_DATE.toString()));
    }

    @Test
    void getNonExistingBookingOrder() {
        // Get the bookingOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBookingOrder() throws Exception {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();

        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        bookingOrderSearchRepository.save(bookingOrder).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());

        // Update the bookingOrder
        BookingOrder updatedBookingOrder = bookingOrderRepository.findById(bookingOrder.getId()).block();
        updatedBookingOrder
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .bookingExpieryDate(UPDATED_BOOKING_EXPIERY_DATE);
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(updatedBookingOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bookingOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        BookingOrder testBookingOrder = bookingOrderList.get(bookingOrderList.size() - 1);
        assertThat(testBookingOrder.getPlacedDate()).isEqualTo(UPDATED_PLACED_DATE);
        assertThat(testBookingOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBookingOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testBookingOrder.getBookingExpieryDate()).isEqualTo(UPDATED_BOOKING_EXPIERY_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BookingOrder> bookingOrderSearchList = IterableUtils.toList(
                    bookingOrderSearchRepository.findAll().collectList().block()
                );
                BookingOrder testBookingOrderSearch = bookingOrderSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBookingOrderSearch.getPlacedDate()).isEqualTo(UPDATED_PLACED_DATE);
                assertThat(testBookingOrderSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testBookingOrderSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testBookingOrderSearch.getBookingExpieryDate()).isEqualTo(UPDATED_BOOKING_EXPIERY_DATE);
            });
    }

    @Test
    void putNonExistingBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bookingOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBookingOrderWithPatch() throws Exception {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();

        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();

        // Update the bookingOrder using partial update
        BookingOrder partialUpdatedBookingOrder = new BookingOrder();
        partialUpdatedBookingOrder.setId(bookingOrder.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBookingOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBookingOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        BookingOrder testBookingOrder = bookingOrderList.get(bookingOrderList.size() - 1);
        assertThat(testBookingOrder.getPlacedDate()).isEqualTo(DEFAULT_PLACED_DATE);
        assertThat(testBookingOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBookingOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testBookingOrder.getBookingExpieryDate()).isEqualTo(DEFAULT_BOOKING_EXPIERY_DATE);
    }

    @Test
    void fullUpdateBookingOrderWithPatch() throws Exception {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();

        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();

        // Update the bookingOrder using partial update
        BookingOrder partialUpdatedBookingOrder = new BookingOrder();
        partialUpdatedBookingOrder.setId(bookingOrder.getId());

        partialUpdatedBookingOrder
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .bookingExpieryDate(UPDATED_BOOKING_EXPIERY_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBookingOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBookingOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        BookingOrder testBookingOrder = bookingOrderList.get(bookingOrderList.size() - 1);
        assertThat(testBookingOrder.getPlacedDate()).isEqualTo(UPDATED_PLACED_DATE);
        assertThat(testBookingOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBookingOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testBookingOrder.getBookingExpieryDate()).isEqualTo(UPDATED_BOOKING_EXPIERY_DATE);
    }

    @Test
    void patchNonExistingBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bookingOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBookingOrder() throws Exception {
        int databaseSizeBeforeUpdate = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        bookingOrder.setId(count.incrementAndGet());

        // Create the BookingOrder
        BookingOrderDTO bookingOrderDTO = bookingOrderMapper.toDto(bookingOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookingOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BookingOrder in the database
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBookingOrder() {
        // Initialize the database
        bookingOrderRepository.save(bookingOrder).block();
        bookingOrderRepository.save(bookingOrder).block();
        bookingOrderSearchRepository.save(bookingOrder).block();

        int databaseSizeBeforeDelete = bookingOrderRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bookingOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bookingOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<BookingOrder> bookingOrderList = bookingOrderRepository.findAll().collectList().block();
        assertThat(bookingOrderList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bookingOrderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBookingOrder() {
        // Initialize the database
        bookingOrder = bookingOrderRepository.save(bookingOrder).block();
        bookingOrderSearchRepository.save(bookingOrder).block();

        // Search the bookingOrder
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + bookingOrder.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(bookingOrder.getId().intValue()))
            .jsonPath("$.[*].placedDate")
            .value(hasItem(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].bookingExpieryDate")
            .value(hasItem(DEFAULT_BOOKING_EXPIERY_DATE.toString()));
    }
}
