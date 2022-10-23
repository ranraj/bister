package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Notification;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.domain.enumeration.NotificationMode;
import com.yalisoft.bister.domain.enumeration.NotificationSourceType;
import com.yalisoft.bister.domain.enumeration.NotificationType;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.NotificationRepository;
import com.yalisoft.bister.repository.search.NotificationSearchRepository;
import com.yalisoft.bister.service.NotificationService;
import com.yalisoft.bister.service.dto.NotificationDTO;
import com.yalisoft.bister.service.mapper.NotificationMapper;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_GOOGLE_NOTIFICATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_GOOGLE_NOTIFICATION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_WHATSAPP_NOTIFICATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_WHATSAPP_NOTIFICATION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SMS_NOTIFICATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_SMS_NOTIFICATION_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final Long DEFAULT_SCHEDULE_ID = 1L;
    private static final Long UPDATED_SCHEDULE_ID = 2L;

    private static final Long DEFAULT_PROMOTION_ID = 1L;
    private static final Long UPDATED_PROMOTION_ID = 2L;

    private static final Boolean DEFAULT_READ = false;
    private static final Boolean UPDATED_READ = true;

    private static final NotificationSourceType DEFAULT_NOTIFICATION_SOURCE_TYPE = NotificationSourceType.PROMOTION;
    private static final NotificationSourceType UPDATED_NOTIFICATION_SOURCE_TYPE = NotificationSourceType.PROJECT;

    private static final NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.INPERSON;
    private static final NotificationType UPDATED_NOTIFICATION_TYPE = NotificationType.PHYSICAL;

    private static final NotificationMode DEFAULT_NOTIFICATION_MODE = NotificationMode.EMAIL;
    private static final NotificationMode UPDATED_NOTIFICATION_MODE = NotificationMode.SMS;

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    @Autowired
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationService notificationServiceMock;

    @Autowired
    private NotificationSearchRepository notificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS)
            .sentDate(DEFAULT_SENT_DATE)
            .googleNotificationId(DEFAULT_GOOGLE_NOTIFICATION_ID)
            .whatsappNotificationId(DEFAULT_WHATSAPP_NOTIFICATION_ID)
            .smsNotificationId(DEFAULT_SMS_NOTIFICATION_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .projectId(DEFAULT_PROJECT_ID)
            .scheduleId(DEFAULT_SCHEDULE_ID)
            .promotionId(DEFAULT_PROMOTION_ID)
            .read(DEFAULT_READ)
            .notificationSourceType(DEFAULT_NOTIFICATION_SOURCE_TYPE)
            .notificationType(DEFAULT_NOTIFICATION_TYPE)
            .notificationMode(DEFAULT_NOTIFICATION_MODE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        notification.setUser(user);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .googleNotificationId(UPDATED_GOOGLE_NOTIFICATION_ID)
            .whatsappNotificationId(UPDATED_WHATSAPP_NOTIFICATION_ID)
            .smsNotificationId(UPDATED_SMS_NOTIFICATION_ID)
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .scheduleId(UPDATED_SCHEDULE_ID)
            .promotionId(UPDATED_PROMOTION_ID)
            .read(UPDATED_READ)
            .notificationSourceType(UPDATED_NOTIFICATION_SOURCE_TYPE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationMode(UPDATED_NOTIFICATION_MODE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        notification.setUser(user);
        return notification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Notification.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        notificationSearchRepository.deleteAll().block();
        assertThat(notificationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        notification = createEntity(em);
    }

    @Test
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(DEFAULT_SENT_DATE);
        assertThat(testNotification.getGoogleNotificationId()).isEqualTo(DEFAULT_GOOGLE_NOTIFICATION_ID);
        assertThat(testNotification.getWhatsappNotificationId()).isEqualTo(DEFAULT_WHATSAPP_NOTIFICATION_ID);
        assertThat(testNotification.getSmsNotificationId()).isEqualTo(DEFAULT_SMS_NOTIFICATION_ID);
        assertThat(testNotification.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testNotification.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testNotification.getScheduleId()).isEqualTo(DEFAULT_SCHEDULE_ID);
        assertThat(testNotification.getPromotionId()).isEqualTo(DEFAULT_PROMOTION_ID);
        assertThat(testNotification.getRead()).isEqualTo(DEFAULT_READ);
        assertThat(testNotification.getNotificationSourceType()).isEqualTo(DEFAULT_NOTIFICATION_SOURCE_TYPE);
        assertThat(testNotification.getNotificationType()).isEqualTo(DEFAULT_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationMode()).isEqualTo(DEFAULT_NOTIFICATION_MODE);
    }

    @Test
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // set the field null
        notification.setDate(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSentDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // set the field null
        notification.setSentDate(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReadIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // set the field null
        notification.setRead(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationSourceTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // set the field null
        notification.setNotificationSourceType(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationModeIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        // set the field null
        notification.setNotificationMode(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllNotifications() {
        // Initialize the database
        notificationRepository.save(notification).block();

        // Get all the notificationList
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
            .value(hasItem(notification.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].sentDate")
            .value(hasItem(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.[*].googleNotificationId")
            .value(hasItem(DEFAULT_GOOGLE_NOTIFICATION_ID))
            .jsonPath("$.[*].whatsappNotificationId")
            .value(hasItem(DEFAULT_WHATSAPP_NOTIFICATION_ID))
            .jsonPath("$.[*].smsNotificationId")
            .value(hasItem(DEFAULT_SMS_NOTIFICATION_ID))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].scheduleId")
            .value(hasItem(DEFAULT_SCHEDULE_ID.intValue()))
            .jsonPath("$.[*].promotionId")
            .value(hasItem(DEFAULT_PROMOTION_ID.intValue()))
            .jsonPath("$.[*].read")
            .value(hasItem(DEFAULT_READ.booleanValue()))
            .jsonPath("$.[*].notificationSourceType")
            .value(hasItem(DEFAULT_NOTIFICATION_SOURCE_TYPE.toString()))
            .jsonPath("$.[*].notificationType")
            .value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString()))
            .jsonPath("$.[*].notificationMode")
            .value(hasItem(DEFAULT_NOTIFICATION_MODE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsEnabled() {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(notificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsNotEnabled() {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(notificationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getNotification() {
        // Initialize the database
        notificationRepository.save(notification).block();

        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notification.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.sentDate")
            .value(is(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.googleNotificationId")
            .value(is(DEFAULT_GOOGLE_NOTIFICATION_ID))
            .jsonPath("$.whatsappNotificationId")
            .value(is(DEFAULT_WHATSAPP_NOTIFICATION_ID))
            .jsonPath("$.smsNotificationId")
            .value(is(DEFAULT_SMS_NOTIFICATION_ID))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.projectId")
            .value(is(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.scheduleId")
            .value(is(DEFAULT_SCHEDULE_ID.intValue()))
            .jsonPath("$.promotionId")
            .value(is(DEFAULT_PROMOTION_ID.intValue()))
            .jsonPath("$.read")
            .value(is(DEFAULT_READ.booleanValue()))
            .jsonPath("$.notificationSourceType")
            .value(is(DEFAULT_NOTIFICATION_SOURCE_TYPE.toString()))
            .jsonPath("$.notificationType")
            .value(is(DEFAULT_NOTIFICATION_TYPE.toString()))
            .jsonPath("$.notificationMode")
            .value(is(DEFAULT_NOTIFICATION_MODE.toString()));
    }

    @Test
    void getNonExistingNotification() {
        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingNotification() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notificationSearchRepository.save(notification).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).block();
        updatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .googleNotificationId(UPDATED_GOOGLE_NOTIFICATION_ID)
            .whatsappNotificationId(UPDATED_WHATSAPP_NOTIFICATION_ID)
            .smsNotificationId(UPDATED_SMS_NOTIFICATION_ID)
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .scheduleId(UPDATED_SCHEDULE_ID)
            .promotionId(UPDATED_PROMOTION_ID)
            .read(UPDATED_READ)
            .notificationSourceType(UPDATED_NOTIFICATION_SOURCE_TYPE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationMode(UPDATED_NOTIFICATION_MODE);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getGoogleNotificationId()).isEqualTo(UPDATED_GOOGLE_NOTIFICATION_ID);
        assertThat(testNotification.getWhatsappNotificationId()).isEqualTo(UPDATED_WHATSAPP_NOTIFICATION_ID);
        assertThat(testNotification.getSmsNotificationId()).isEqualTo(UPDATED_SMS_NOTIFICATION_ID);
        assertThat(testNotification.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testNotification.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testNotification.getScheduleId()).isEqualTo(UPDATED_SCHEDULE_ID);
        assertThat(testNotification.getPromotionId()).isEqualTo(UPDATED_PROMOTION_ID);
        assertThat(testNotification.getRead()).isEqualTo(UPDATED_READ);
        assertThat(testNotification.getNotificationSourceType()).isEqualTo(UPDATED_NOTIFICATION_SOURCE_TYPE);
        assertThat(testNotification.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationMode()).isEqualTo(UPDATED_NOTIFICATION_MODE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Notification> notificationSearchList = IterableUtils.toList(
                    notificationSearchRepository.findAll().collectList().block()
                );
                Notification testNotificationSearch = notificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testNotificationSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testNotificationSearch.getDetails()).isEqualTo(UPDATED_DETAILS);
                assertThat(testNotificationSearch.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
                assertThat(testNotificationSearch.getGoogleNotificationId()).isEqualTo(UPDATED_GOOGLE_NOTIFICATION_ID);
                assertThat(testNotificationSearch.getWhatsappNotificationId()).isEqualTo(UPDATED_WHATSAPP_NOTIFICATION_ID);
                assertThat(testNotificationSearch.getSmsNotificationId()).isEqualTo(UPDATED_SMS_NOTIFICATION_ID);
                assertThat(testNotificationSearch.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
                assertThat(testNotificationSearch.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
                assertThat(testNotificationSearch.getScheduleId()).isEqualTo(UPDATED_SCHEDULE_ID);
                assertThat(testNotificationSearch.getPromotionId()).isEqualTo(UPDATED_PROMOTION_ID);
                assertThat(testNotificationSearch.getRead()).isEqualTo(UPDATED_READ);
                assertThat(testNotificationSearch.getNotificationSourceType()).isEqualTo(UPDATED_NOTIFICATION_SOURCE_TYPE);
                assertThat(testNotificationSearch.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
                assertThat(testNotificationSearch.getNotificationMode()).isEqualTo(UPDATED_NOTIFICATION_MODE);
            });
    }

    @Test
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .sentDate(UPDATED_SENT_DATE)
            .googleNotificationId(UPDATED_GOOGLE_NOTIFICATION_ID)
            .whatsappNotificationId(UPDATED_WHATSAPP_NOTIFICATION_ID)
            .smsNotificationId(UPDATED_SMS_NOTIFICATION_ID)
            .productId(UPDATED_PRODUCT_ID)
            .scheduleId(UPDATED_SCHEDULE_ID)
            .notificationMode(UPDATED_NOTIFICATION_MODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getGoogleNotificationId()).isEqualTo(UPDATED_GOOGLE_NOTIFICATION_ID);
        assertThat(testNotification.getWhatsappNotificationId()).isEqualTo(UPDATED_WHATSAPP_NOTIFICATION_ID);
        assertThat(testNotification.getSmsNotificationId()).isEqualTo(UPDATED_SMS_NOTIFICATION_ID);
        assertThat(testNotification.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testNotification.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testNotification.getScheduleId()).isEqualTo(UPDATED_SCHEDULE_ID);
        assertThat(testNotification.getPromotionId()).isEqualTo(DEFAULT_PROMOTION_ID);
        assertThat(testNotification.getRead()).isEqualTo(DEFAULT_READ);
        assertThat(testNotification.getNotificationSourceType()).isEqualTo(DEFAULT_NOTIFICATION_SOURCE_TYPE);
        assertThat(testNotification.getNotificationType()).isEqualTo(DEFAULT_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationMode()).isEqualTo(UPDATED_NOTIFICATION_MODE);
    }

    @Test
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .sentDate(UPDATED_SENT_DATE)
            .googleNotificationId(UPDATED_GOOGLE_NOTIFICATION_ID)
            .whatsappNotificationId(UPDATED_WHATSAPP_NOTIFICATION_ID)
            .smsNotificationId(UPDATED_SMS_NOTIFICATION_ID)
            .productId(UPDATED_PRODUCT_ID)
            .projectId(UPDATED_PROJECT_ID)
            .scheduleId(UPDATED_SCHEDULE_ID)
            .promotionId(UPDATED_PROMOTION_ID)
            .read(UPDATED_READ)
            .notificationSourceType(UPDATED_NOTIFICATION_SOURCE_TYPE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationMode(UPDATED_NOTIFICATION_MODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotification.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testNotification.getSentDate()).isEqualTo(UPDATED_SENT_DATE);
        assertThat(testNotification.getGoogleNotificationId()).isEqualTo(UPDATED_GOOGLE_NOTIFICATION_ID);
        assertThat(testNotification.getWhatsappNotificationId()).isEqualTo(UPDATED_WHATSAPP_NOTIFICATION_ID);
        assertThat(testNotification.getSmsNotificationId()).isEqualTo(UPDATED_SMS_NOTIFICATION_ID);
        assertThat(testNotification.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testNotification.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testNotification.getScheduleId()).isEqualTo(UPDATED_SCHEDULE_ID);
        assertThat(testNotification.getPromotionId()).isEqualTo(UPDATED_PROMOTION_ID);
        assertThat(testNotification.getRead()).isEqualTo(UPDATED_READ);
        assertThat(testNotification.getNotificationSourceType()).isEqualTo(UPDATED_NOTIFICATION_SOURCE_TYPE);
        assertThat(testNotification.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationMode()).isEqualTo(UPDATED_NOTIFICATION_MODE);
    }

    @Test
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteNotification() {
        // Initialize the database
        notificationRepository.save(notification).block();
        notificationRepository.save(notification).block();
        notificationSearchRepository.save(notification).block();

        int databaseSizeBeforeDelete = notificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchNotification() {
        // Initialize the database
        notification = notificationRepository.save(notification).block();
        notificationSearchRepository.save(notification).block();

        // Search the notification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + notification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(notification.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].sentDate")
            .value(hasItem(DEFAULT_SENT_DATE.toString()))
            .jsonPath("$.[*].googleNotificationId")
            .value(hasItem(DEFAULT_GOOGLE_NOTIFICATION_ID))
            .jsonPath("$.[*].whatsappNotificationId")
            .value(hasItem(DEFAULT_WHATSAPP_NOTIFICATION_ID))
            .jsonPath("$.[*].smsNotificationId")
            .value(hasItem(DEFAULT_SMS_NOTIFICATION_ID))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].projectId")
            .value(hasItem(DEFAULT_PROJECT_ID.intValue()))
            .jsonPath("$.[*].scheduleId")
            .value(hasItem(DEFAULT_SCHEDULE_ID.intValue()))
            .jsonPath("$.[*].promotionId")
            .value(hasItem(DEFAULT_PROMOTION_ID.intValue()))
            .jsonPath("$.[*].read")
            .value(hasItem(DEFAULT_READ.booleanValue()))
            .jsonPath("$.[*].notificationSourceType")
            .value(hasItem(DEFAULT_NOTIFICATION_SOURCE_TYPE.toString()))
            .jsonPath("$.[*].notificationType")
            .value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString()))
            .jsonPath("$.[*].notificationMode")
            .value(hasItem(DEFAULT_NOTIFICATION_MODE.toString()));
    }
}
