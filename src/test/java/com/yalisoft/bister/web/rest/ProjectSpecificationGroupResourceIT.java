package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.ProjectSpecificationGroupRepository;
import com.yalisoft.bister.repository.search.ProjectSpecificationGroupSearchRepository;
import com.yalisoft.bister.service.ProjectSpecificationGroupService;
import com.yalisoft.bister.service.dto.ProjectSpecificationGroupDTO;
import com.yalisoft.bister.service.mapper.ProjectSpecificationGroupMapper;
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
 * Integration tests for the {@link ProjectSpecificationGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectSpecificationGroupResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/project-specification-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-specification-groups";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectSpecificationGroupRepository projectSpecificationGroupRepository;

    @Mock
    private ProjectSpecificationGroupRepository projectSpecificationGroupRepositoryMock;

    @Autowired
    private ProjectSpecificationGroupMapper projectSpecificationGroupMapper;

    @Mock
    private ProjectSpecificationGroupService projectSpecificationGroupServiceMock;

    @Autowired
    private ProjectSpecificationGroupSearchRepository projectSpecificationGroupSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectSpecificationGroup projectSpecificationGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSpecificationGroup createEntity(EntityManager em) {
        ProjectSpecificationGroup projectSpecificationGroup = new ProjectSpecificationGroup()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION);
        return projectSpecificationGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSpecificationGroup createUpdatedEntity(EntityManager em) {
        ProjectSpecificationGroup projectSpecificationGroup = new ProjectSpecificationGroup()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION);
        return projectSpecificationGroup;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectSpecificationGroup.class).block();
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
        projectSpecificationGroupSearchRepository.deleteAll().block();
        assertThat(projectSpecificationGroupSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        projectSpecificationGroup = createEntity(em);
    }

    @Test
    void createProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeCreate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    projectSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProjectSpecificationGroup testProjectSpecificationGroup = projectSpecificationGroupList.get(
            projectSpecificationGroupList.size() - 1
        );
        assertThat(testProjectSpecificationGroup.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProjectSpecificationGroup.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProjectSpecificationGroup.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createProjectSpecificationGroupWithExistingId() throws Exception {
        // Create the ProjectSpecificationGroup with an existing ID
        projectSpecificationGroup.setId(1L);
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        int databaseSizeBeforeCreate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        // set the field null
        projectSpecificationGroup.setTitle(null);

        // Create the ProjectSpecificationGroup, which fails.
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProjectSpecificationGroups() {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();

        // Get all the projectSpecificationGroupList
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
            .value(hasItem(projectSpecificationGroup.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectSpecificationGroupsWithEagerRelationshipsIsEnabled() {
        when(projectSpecificationGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(projectSpecificationGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectSpecificationGroupsWithEagerRelationshipsIsNotEnabled() {
        when(projectSpecificationGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(projectSpecificationGroupRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProjectSpecificationGroup() {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();

        // Get the projectSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectSpecificationGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectSpecificationGroup.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingProjectSpecificationGroup() {
        // Get the projectSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectSpecificationGroup() throws Exception {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();

        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        projectSpecificationGroupSearchRepository.save(projectSpecificationGroup).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());

        // Update the projectSpecificationGroup
        ProjectSpecificationGroup updatedProjectSpecificationGroup = projectSpecificationGroupRepository
            .findById(projectSpecificationGroup.getId())
            .block();
        updatedProjectSpecificationGroup.title(UPDATED_TITLE).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION);
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(updatedProjectSpecificationGroup);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSpecificationGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecificationGroup testProjectSpecificationGroup = projectSpecificationGroupList.get(
            projectSpecificationGroupList.size() - 1
        );
        assertThat(testProjectSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecificationGroup.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProjectSpecificationGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    projectSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProjectSpecificationGroup> projectSpecificationGroupSearchList = IterableUtils.toList(
                    projectSpecificationGroupSearchRepository.findAll().collectList().block()
                );
                ProjectSpecificationGroup testProjectSpecificationGroupSearch = projectSpecificationGroupSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testProjectSpecificationGroupSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testProjectSpecificationGroupSearch.getSlug()).isEqualTo(UPDATED_SLUG);
                assertThat(testProjectSpecificationGroupSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSpecificationGroupDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProjectSpecificationGroupWithPatch() throws Exception {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();

        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();

        // Update the projectSpecificationGroup using partial update
        ProjectSpecificationGroup partialUpdatedProjectSpecificationGroup = new ProjectSpecificationGroup();
        partialUpdatedProjectSpecificationGroup.setId(projectSpecificationGroup.getId());

        partialUpdatedProjectSpecificationGroup.title(UPDATED_TITLE).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSpecificationGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSpecificationGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecificationGroup testProjectSpecificationGroup = projectSpecificationGroupList.get(
            projectSpecificationGroupList.size() - 1
        );
        assertThat(testProjectSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecificationGroup.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProjectSpecificationGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateProjectSpecificationGroupWithPatch() throws Exception {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();

        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();

        // Update the projectSpecificationGroup using partial update
        ProjectSpecificationGroup partialUpdatedProjectSpecificationGroup = new ProjectSpecificationGroup();
        partialUpdatedProjectSpecificationGroup.setId(projectSpecificationGroup.getId());

        partialUpdatedProjectSpecificationGroup.title(UPDATED_TITLE).slug(UPDATED_SLUG).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSpecificationGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSpecificationGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        ProjectSpecificationGroup testProjectSpecificationGroup = projectSpecificationGroupList.get(
            projectSpecificationGroupList.size() - 1
        );
        assertThat(testProjectSpecificationGroup.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProjectSpecificationGroup.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProjectSpecificationGroup.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectSpecificationGroupDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProjectSpecificationGroup() throws Exception {
        int databaseSizeBeforeUpdate = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        projectSpecificationGroup.setId(count.incrementAndGet());

        // Create the ProjectSpecificationGroup
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = projectSpecificationGroupMapper.toDto(projectSpecificationGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projectSpecificationGroupDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSpecificationGroup in the database
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProjectSpecificationGroup() {
        // Initialize the database
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();
        projectSpecificationGroupRepository.save(projectSpecificationGroup).block();
        projectSpecificationGroupSearchRepository.save(projectSpecificationGroup).block();

        int databaseSizeBeforeDelete = projectSpecificationGroupRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the projectSpecificationGroup
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectSpecificationGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProjectSpecificationGroup> projectSpecificationGroupList = projectSpecificationGroupRepository.findAll().collectList().block();
        assertThat(projectSpecificationGroupList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSpecificationGroupSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProjectSpecificationGroup() {
        // Initialize the database
        projectSpecificationGroup = projectSpecificationGroupRepository.save(projectSpecificationGroup).block();
        projectSpecificationGroupSearchRepository.save(projectSpecificationGroup).block();

        // Search the projectSpecificationGroup
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + projectSpecificationGroup.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectSpecificationGroup.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
