package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.ProjectType;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProjectTypeRepository;
import com.yalisoft.bister.repository.search.ProjectTypeSearchRepository;
import com.yalisoft.bister.service.dto.ProjectTypeDTO;
import com.yalisoft.bister.service.mapper.ProjectTypeMapper;
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
 * Integration tests for the {@link ProjectTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/project-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private ProjectTypeMapper projectTypeMapper;

    @Autowired
    private ProjectTypeSearchRepository projectTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectType projectType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectType createEntity(EntityManager em) {
        ProjectType projectType = new ProjectType().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return projectType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectType createUpdatedEntity(EntityManager em) {
        ProjectType projectType = new ProjectType().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return projectType;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectType.class).block();
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
        projectTypeSearchRepository.deleteAll().block();
        assertThat(projectTypeSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        projectType = createEntity(em);
    }

    @Test
    void createProjectType() throws Exception {
        int databaseSizeBeforeCreate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProjectType testProjectType = projectTypeList.get(projectTypeList.size() - 1);
        assertThat(testProjectType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProjectType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createProjectTypeWithExistingId() throws Exception {
        // Create the ProjectType with an existing ID
        projectType.setId(1L);
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        int databaseSizeBeforeCreate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        // set the field null
        projectType.setName(null);

        // Create the ProjectType, which fails.
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProjectTypes() {
        // Initialize the database
        projectTypeRepository.save(projectType).block();

        // Get all the projectTypeList
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
            .value(hasItem(projectType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getProjectType() {
        // Initialize the database
        projectTypeRepository.save(projectType).block();

        // Get the projectType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectType.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingProjectType() {
        // Get the projectType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectType() throws Exception {
        // Initialize the database
        projectTypeRepository.save(projectType).block();

        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        projectTypeSearchRepository.save(projectType).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());

        // Update the projectType
        ProjectType updatedProjectType = projectTypeRepository.findById(projectType.getId()).block();
        updatedProjectType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(updatedProjectType);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        ProjectType testProjectType = projectTypeList.get(projectTypeList.size() - 1);
        assertThat(testProjectType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProjectType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProjectType> projectTypeSearchList = IterableUtils.toList(projectTypeSearchRepository.findAll().collectList().block());
                ProjectType testProjectTypeSearch = projectTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProjectTypeSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProjectTypeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProjectTypeWithPatch() throws Exception {
        // Initialize the database
        projectTypeRepository.save(projectType).block();

        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();

        // Update the projectType using partial update
        ProjectType partialUpdatedProjectType = new ProjectType();
        partialUpdatedProjectType.setId(projectType.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        ProjectType testProjectType = projectTypeList.get(projectTypeList.size() - 1);
        assertThat(testProjectType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProjectType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateProjectTypeWithPatch() throws Exception {
        // Initialize the database
        projectTypeRepository.save(projectType).block();

        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();

        // Update the projectType using partial update
        ProjectType partialUpdatedProjectType = new ProjectType();
        partialUpdatedProjectType.setId(projectType.getId());

        partialUpdatedProjectType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        ProjectType testProjectType = projectTypeList.get(projectTypeList.size() - 1);
        assertThat(testProjectType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProjectType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectTypeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProjectType() throws Exception {
        int databaseSizeBeforeUpdate = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        projectType.setId(count.incrementAndGet());

        // Create the ProjectType
        ProjectTypeDTO projectTypeDTO = projectTypeMapper.toDto(projectType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectType in the database
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProjectType() {
        // Initialize the database
        projectTypeRepository.save(projectType).block();
        projectTypeRepository.save(projectType).block();
        projectTypeSearchRepository.save(projectType).block();

        int databaseSizeBeforeDelete = projectTypeRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the projectType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProjectType> projectTypeList = projectTypeRepository.findAll().collectList().block();
        assertThat(projectTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectTypeSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProjectType() {
        // Initialize the database
        projectType = projectTypeRepository.save(projectType).block();
        projectTypeSearchRepository.save(projectType).block();

        // Search the projectType
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + projectType.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
