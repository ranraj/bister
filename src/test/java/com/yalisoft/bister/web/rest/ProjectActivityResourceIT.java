package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectActivity;
import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProjectActivityRepository;
import com.yalisoft.bister.repository.search.ProjectActivitySearchRepository;
import com.yalisoft.bister.service.ProjectActivityService;
import com.yalisoft.bister.service.dto.ProjectActivityDTO;
import com.yalisoft.bister.service.mapper.ProjectActivityMapper;
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
 * Integration tests for the {@link ProjectActivityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectActivityResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBBBBBBBBBBBB";

    private static final ActivityStatus DEFAULT_STATUS = ActivityStatus.NEW;
    private static final ActivityStatus UPDATED_STATUS = ActivityStatus.INPROGRESS;

    private static final String ENTITY_API_URL = "/api/project-activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-activities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectActivityRepository projectActivityRepository;

    @Mock
    private ProjectActivityRepository projectActivityRepositoryMock;

    @Autowired
    private ProjectActivityMapper projectActivityMapper;

    @Mock
    private ProjectActivityService projectActivityServiceMock;

    @Autowired
    private ProjectActivitySearchRepository projectActivitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectActivity projectActivity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectActivity createEntity(EntityManager em) {
        ProjectActivity projectActivity = new ProjectActivity().title(DEFAULT_TITLE).details(DEFAULT_DETAILS).status(DEFAULT_STATUS);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity(em)).block();
        projectActivity.setProject(project);
        return projectActivity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectActivity createUpdatedEntity(EntityManager em) {
        ProjectActivity projectActivity = new ProjectActivity().title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity(em)).block();
        projectActivity.setProject(project);
        return projectActivity;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectActivity.class).block();
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
        projectActivitySearchRepository.deleteAll().block();
        assertThat(projectActivitySearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        projectActivity = createEntity(em);
    }

    @Test
    void createProjectActivity() throws Exception {
        int databaseSizeBeforeCreate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProjectActivity testProjectActivity = projectActivityList.get(projectActivityList.size() - 1);
        assertThat(testProjectActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProjectActivity.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testProjectActivity.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createProjectActivityWithExistingId() throws Exception {
        // Create the ProjectActivity with an existing ID
        projectActivity.setId(1L);
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        int databaseSizeBeforeCreate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        // set the field null
        projectActivity.setTitle(null);

        // Create the ProjectActivity, which fails.
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        // set the field null
        projectActivity.setStatus(null);

        // Create the ProjectActivity, which fails.
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProjectActivities() {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();

        // Get all the projectActivityList
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
            .value(hasItem(projectActivity.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectActivitiesWithEagerRelationshipsIsEnabled() {
        when(projectActivityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(projectActivityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectActivitiesWithEagerRelationshipsIsNotEnabled() {
        when(projectActivityServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(projectActivityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProjectActivity() {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();

        // Get the projectActivity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectActivity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectActivity.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingProjectActivity() {
        // Get the projectActivity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectActivity() throws Exception {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();

        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        projectActivitySearchRepository.save(projectActivity).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());

        // Update the projectActivity
        ProjectActivity updatedProjectActivity = projectActivityRepository.findById(projectActivity.getId()).block();
        updatedProjectActivity.title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(updatedProjectActivity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectActivityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        ProjectActivity testProjectActivity = projectActivityList.get(projectActivityList.size() - 1);
        assertThat(testProjectActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectActivity.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProjectActivity.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProjectActivity> projectActivitySearchList = IterableUtils.toList(
                    projectActivitySearchRepository.findAll().collectList().block()
                );
                ProjectActivity testProjectActivitySearch = projectActivitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProjectActivitySearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProjectActivitySearch.getDetails()).isEqualTo(UPDATED_DETAILS);
                assertThat(testProjectActivitySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    void putNonExistingProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectActivityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProjectActivityWithPatch() throws Exception {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();

        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();

        // Update the projectActivity using partial update
        ProjectActivity partialUpdatedProjectActivity = new ProjectActivity();
        partialUpdatedProjectActivity.setId(projectActivity.getId());

        partialUpdatedProjectActivity.details(UPDATED_DETAILS).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectActivity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectActivity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        ProjectActivity testProjectActivity = projectActivityList.get(projectActivityList.size() - 1);
        assertThat(testProjectActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProjectActivity.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProjectActivity.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateProjectActivityWithPatch() throws Exception {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();

        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();

        // Update the projectActivity using partial update
        ProjectActivity partialUpdatedProjectActivity = new ProjectActivity();
        partialUpdatedProjectActivity.setId(projectActivity.getId());

        partialUpdatedProjectActivity.title(UPDATED_TITLE).details(UPDATED_DETAILS).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectActivity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectActivity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        ProjectActivity testProjectActivity = projectActivityList.get(projectActivityList.size() - 1);
        assertThat(testProjectActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectActivity.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testProjectActivity.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectActivityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProjectActivity() throws Exception {
        int databaseSizeBeforeUpdate = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        projectActivity.setId(count.incrementAndGet());

        // Create the ProjectActivity
        ProjectActivityDTO projectActivityDTO = projectActivityMapper.toDto(projectActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectActivityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectActivity in the database
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProjectActivity() {
        // Initialize the database
        projectActivityRepository.save(projectActivity).block();
        projectActivityRepository.save(projectActivity).block();
        projectActivitySearchRepository.save(projectActivity).block();

        int databaseSizeBeforeDelete = projectActivityRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the projectActivity
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectActivity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProjectActivity> projectActivityList = projectActivityRepository.findAll().collectList().block();
        assertThat(projectActivityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectActivitySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProjectActivity() {
        // Initialize the database
        projectActivity = projectActivityRepository.save(projectActivity).block();
        projectActivitySearchRepository.save(projectActivity).block();

        // Search the projectActivity
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + projectActivity.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectActivity.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }
}
