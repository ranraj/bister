package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectReview;
import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProjectReviewRepository;
import com.yalisoft.bister.repository.search.ProjectReviewSearchRepository;
import com.yalisoft.bister.service.ProjectReviewService;
import com.yalisoft.bister.service.dto.ProjectReviewDTO;
import com.yalisoft.bister.service.mapper.ProjectReviewMapper;
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
 * Integration tests for the {@link ProjectReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectReviewResourceIT {

    private static final String DEFAULT_REVIEWER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_REVIEWER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REVIEWER_EMAIL = "np@F7f.Gfd6";
    private static final String UPDATED_REVIEWER_EMAIL = "X@rkM2.Q";

    private static final String DEFAULT_REVIEW =
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_REVIEW =
        "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final ReviewStatus DEFAULT_STATUS = ReviewStatus.APPROVED;
    private static final ReviewStatus UPDATED_STATUS = ReviewStatus.HOLD;

    private static final Long DEFAULT_REVIEWER_ID = 1L;
    private static final Long UPDATED_REVIEWER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/project-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-reviews";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectReviewRepository projectReviewRepository;

    @Mock
    private ProjectReviewRepository projectReviewRepositoryMock;

    @Autowired
    private ProjectReviewMapper projectReviewMapper;

    @Mock
    private ProjectReviewService projectReviewServiceMock;

    @Autowired
    private ProjectReviewSearchRepository projectReviewSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectReview projectReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectReview createEntity(EntityManager em) {
        ProjectReview projectReview = new ProjectReview()
            .reviewerName(DEFAULT_REVIEWER_NAME)
            .reviewerEmail(DEFAULT_REVIEWER_EMAIL)
            .review(DEFAULT_REVIEW)
            .rating(DEFAULT_RATING)
            .status(DEFAULT_STATUS)
            .reviewerId(DEFAULT_REVIEWER_ID);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity(em)).block();
        projectReview.setProject(project);
        return projectReview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectReview createUpdatedEntity(EntityManager em) {
        ProjectReview projectReview = new ProjectReview()
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity(em)).block();
        projectReview.setProject(project);
        return projectReview;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectReview.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProjectResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        projectReviewSearchRepository.deleteAll().block();
        assertThat(projectReviewSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        projectReview = createEntity(em);
    }

    @Test
    void createProjectReview() throws Exception {
        int databaseSizeBeforeCreate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProjectReview testProjectReview = projectReviewList.get(projectReviewList.size() - 1);
        assertThat(testProjectReview.getReviewerName()).isEqualTo(DEFAULT_REVIEWER_NAME);
        assertThat(testProjectReview.getReviewerEmail()).isEqualTo(DEFAULT_REVIEWER_EMAIL);
        assertThat(testProjectReview.getReview()).isEqualTo(DEFAULT_REVIEW);
        assertThat(testProjectReview.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProjectReview.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProjectReview.getReviewerId()).isEqualTo(DEFAULT_REVIEWER_ID);
    }

    @Test
    void createProjectReviewWithExistingId() throws Exception {
        // Create the ProjectReview with an existing ID
        projectReview.setId(1L);
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        int databaseSizeBeforeCreate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setReviewerName(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setReviewerEmail(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setReview(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setRating(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setStatus(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReviewerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        // set the field null
        projectReview.setReviewerId(null);

        // Create the ProjectReview, which fails.
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProjectReviews() {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();

        // Get all the projectReviewList
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
            .value(hasItem(projectReview.getId().intValue()))
            .jsonPath("$.[*].reviewerName")
            .value(hasItem(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.[*].reviewerEmail")
            .value(hasItem(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.[*].review")
            .value(hasItem(DEFAULT_REVIEW))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reviewerId")
            .value(hasItem(DEFAULT_REVIEWER_ID.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectReviewsWithEagerRelationshipsIsEnabled() {
        when(projectReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(projectReviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectReviewsWithEagerRelationshipsIsNotEnabled() {
        when(projectReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(projectReviewRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProjectReview() {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();

        // Get the projectReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectReview.getId().intValue()))
            .jsonPath("$.reviewerName")
            .value(is(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.reviewerEmail")
            .value(is(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.review")
            .value(is(DEFAULT_REVIEW))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.reviewerId")
            .value(is(DEFAULT_REVIEWER_ID.intValue()));
    }

    @Test
    void getNonExistingProjectReview() {
        // Get the projectReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectReview() throws Exception {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();

        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        projectReviewSearchRepository.save(projectReview).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());

        // Update the projectReview
        ProjectReview updatedProjectReview = projectReviewRepository.findById(projectReview.getId()).block();
        updatedProjectReview
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(updatedProjectReview);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        ProjectReview testProjectReview = projectReviewList.get(projectReviewList.size() - 1);
        assertThat(testProjectReview.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
        assertThat(testProjectReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProjectReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProjectReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProjectReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProjectReview.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProjectReview> projectReviewSearchList = IterableUtils.toList(
                    projectReviewSearchRepository.findAll().collectList().block()
                );
                ProjectReview testProjectReviewSearch = projectReviewSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProjectReviewSearch.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
                assertThat(testProjectReviewSearch.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
                assertThat(testProjectReviewSearch.getReview()).isEqualTo(UPDATED_REVIEW);
                assertThat(testProjectReviewSearch.getRating()).isEqualTo(UPDATED_RATING);
                assertThat(testProjectReviewSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testProjectReviewSearch.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
            });
    }

    @Test
    void putNonExistingProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProjectReviewWithPatch() throws Exception {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();

        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();

        // Update the projectReview using partial update
        ProjectReview partialUpdatedProjectReview = new ProjectReview();
        partialUpdatedProjectReview.setId(projectReview.getId());

        partialUpdatedProjectReview.reviewerEmail(UPDATED_REVIEWER_EMAIL).review(UPDATED_REVIEW).rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        ProjectReview testProjectReview = projectReviewList.get(projectReviewList.size() - 1);
        assertThat(testProjectReview.getReviewerName()).isEqualTo(DEFAULT_REVIEWER_NAME);
        assertThat(testProjectReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProjectReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProjectReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProjectReview.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProjectReview.getReviewerId()).isEqualTo(DEFAULT_REVIEWER_ID);
    }

    @Test
    void fullUpdateProjectReviewWithPatch() throws Exception {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();

        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();

        // Update the projectReview using partial update
        ProjectReview partialUpdatedProjectReview = new ProjectReview();
        partialUpdatedProjectReview.setId(projectReview.getId());

        partialUpdatedProjectReview
            .reviewerName(UPDATED_REVIEWER_NAME)
            .reviewerEmail(UPDATED_REVIEWER_EMAIL)
            .review(UPDATED_REVIEW)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .reviewerId(UPDATED_REVIEWER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        ProjectReview testProjectReview = projectReviewList.get(projectReviewList.size() - 1);
        assertThat(testProjectReview.getReviewerName()).isEqualTo(UPDATED_REVIEWER_NAME);
        assertThat(testProjectReview.getReviewerEmail()).isEqualTo(UPDATED_REVIEWER_EMAIL);
        assertThat(testProjectReview.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testProjectReview.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProjectReview.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProjectReview.getReviewerId()).isEqualTo(UPDATED_REVIEWER_ID);
    }

    @Test
    void patchNonExistingProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectReviewDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProjectReview() throws Exception {
        int databaseSizeBeforeUpdate = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        projectReview.setId(count.incrementAndGet());

        // Create the ProjectReview
        ProjectReviewDTO projectReviewDTO = projectReviewMapper.toDto(projectReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectReview in the database
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProjectReview() {
        // Initialize the database
        projectReviewRepository.save(projectReview).block();
        projectReviewRepository.save(projectReview).block();
        projectReviewSearchRepository.save(projectReview).block();

        int databaseSizeBeforeDelete = projectReviewRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the projectReview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProjectReview> projectReviewList = projectReviewRepository.findAll().collectList().block();
        assertThat(projectReviewList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectReviewSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProjectReview() {
        // Initialize the database
        projectReview = projectReviewRepository.save(projectReview).block();
        projectReviewSearchRepository.save(projectReview).block();

        // Search the projectReview
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + projectReview.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectReview.getId().intValue()))
            .jsonPath("$.[*].reviewerName")
            .value(hasItem(DEFAULT_REVIEWER_NAME))
            .jsonPath("$.[*].reviewerEmail")
            .value(hasItem(DEFAULT_REVIEWER_EMAIL))
            .jsonPath("$.[*].review")
            .value(hasItem(DEFAULT_REVIEW))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reviewerId")
            .value(hasItem(DEFAULT_REVIEWER_ID.intValue()));
    }
}
