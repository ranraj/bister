package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectSpecification;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProjectSpecificationRepository;
import com.yalisoft.bister.repository.search.ProjectSpecificationSearchRepository;
import com.yalisoft.bister.service.ProjectSpecificationService;
import com.yalisoft.bister.service.dto.ProjectSpecificationDTO;
import com.yalisoft.bister.service.mapper.ProjectSpecificationMapper;
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
 * Integration tests for the {@link ProjectSpecificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectSpecificationResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/project-specifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-specifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectSpecificationRepository projectSpecificationRepository;

    @Mock
    private ProjectSpecificationRepository projectSpecificationRepositoryMock;

    @Autowired
    private ProjectSpecificationMapper projectSpecificationMapper;

    @Mock
    private ProjectSpecificationService projectSpecificationServiceMock;

    @Autowired
    private ProjectSpecificationSearchRepository projectSpecificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectSpecification projectSpecification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSpecification createEntity(EntityManager em) {
        ProjectSpecification projectSpecification = new ProjectSpecification()
            .title(DEFAULT_TITLE)
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity(em)).block();
        projectSpecification.setProject(project);
        return projectSpecification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSpecification createUpdatedEntity(EntityManager em) {
        ProjectSpecification projectSpecification = new ProjectSpecification()
            .title(UPDATED_TITLE)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity(em)).block();
        projectSpecification.setProject(project);
        return projectSpecification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectSpecification.class).block();
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
        projectSpecificationSearchRepository.deleteAll().block();
        assertThat(projectSpecificationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        projectSpecification = createEntity(em);
    }

    @Test
    void createProjectSpecification() throws Exception {
        int databaseSizeBeforeCreate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProjectSpecification testProjectSpecification = projectSpecificationList.get(projectSpecificationList.size() - 1);
        assertThat(testProjectSpecification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProjectSpecification.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProjectSpecification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createProjectSpecificationWithExistingId() throws Exception {
        // Create the ProjectSpecification with an existing ID
        projectSpecification.setId(1L);
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        int databaseSizeBeforeCreate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        // set the field null
        projectSpecification.setTitle(null);

        // Create the ProjectSpecification, which fails.
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        // set the field null
        projectSpecification.setValue(null);

        // Create the ProjectSpecification, which fails.
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProjectSpecifications() {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();

        // Get all the projectSpecificationList
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
            .value(hasItem(projectSpecification.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectSpecificationsWithEagerRelationshipsIsEnabled() {
        when(projectSpecificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(projectSpecificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectSpecificationsWithEagerRelationshipsIsNotEnabled() {
        when(projectSpecificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(projectSpecificationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProjectSpecification() {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();

        // Get the projectSpecification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectSpecification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectSpecification.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.value")
            .value(is(DEFAULT_VALUE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingProjectSpecification() {
        // Get the projectSpecification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectSpecification() throws Exception {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();

        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        projectSpecificationSearchRepository.save(projectSpecification).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());

        // Update the projectSpecification
        ProjectSpecification updatedProjectSpecification = projectSpecificationRepository.findById(projectSpecification.getId()).block();
        updatedProjectSpecification.title(UPDATED_TITLE).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(updatedProjectSpecification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSpecificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecification testProjectSpecification = projectSpecificationList.get(projectSpecificationList.size() - 1);
        assertThat(testProjectSpecification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProjectSpecification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProjectSpecification> projectSpecificationSearchList = IterableUtils.toList(
                    projectSpecificationSearchRepository.findAll().collectList().block()
                );
                ProjectSpecification testProjectSpecificationSearch = projectSpecificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProjectSpecificationSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProjectSpecificationSearch.getValue()).isEqualTo(UPDATED_VALUE);
                assertThat(testProjectSpecificationSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSpecificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProjectSpecificationWithPatch() throws Exception {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();

        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();

        // Update the projectSpecification using partial update
        ProjectSpecification partialUpdatedProjectSpecification = new ProjectSpecification();
        partialUpdatedProjectSpecification.setId(projectSpecification.getId());

        partialUpdatedProjectSpecification.title(UPDATED_TITLE).value(UPDATED_VALUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSpecification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSpecification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecification testProjectSpecification = projectSpecificationList.get(projectSpecificationList.size() - 1);
        assertThat(testProjectSpecification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProjectSpecification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateProjectSpecificationWithPatch() throws Exception {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();

        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();

        // Update the projectSpecification using partial update
        ProjectSpecification partialUpdatedProjectSpecification = new ProjectSpecification();
        partialUpdatedProjectSpecification.setId(projectSpecification.getId());

        partialUpdatedProjectSpecification.title(UPDATED_TITLE).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSpecification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSpecification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecification testProjectSpecification = projectSpecificationList.get(projectSpecificationList.size() - 1);
        assertThat(testProjectSpecification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecification.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProjectSpecification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectSpecificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProjectSpecification() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        projectSpecification.setId(count.incrementAndGet());

        // Create the ProjectSpecification
        ProjectSpecificationDTO projectSpecificationDTO = projectSpecificationMapper.toDto(projectSpecification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSpecification in the database
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProjectSpecification() {
        // Initialize the database
        projectSpecificationRepository.save(projectSpecification).block();
        projectSpecificationRepository.save(projectSpecification).block();
        projectSpecificationSearchRepository.save(projectSpecification).block();

        int databaseSizeBeforeDelete = projectSpecificationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the projectSpecification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectSpecification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProjectSpecification> projectSpecificationList = projectSpecificationRepository.findAll().collectList().block();
        assertThat(projectSpecificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProjectSpecification() {
        // Initialize the database
        projectSpecification = projectSpecificationRepository.save(projectSpecification).block();
        projectSpecificationSearchRepository.save(projectSpecification).block();

        // Search the projectSpecification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + projectSpecification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectSpecification.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
