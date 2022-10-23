package com.yalisoft.bister.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.yalisoft.bister.IntegrationTest;
import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.domain.enumeration.AttachmentApprovalStatus;
import com.yalisoft.bister.domain.enumeration.AttachmentSourceType;
import com.yalisoft.bister.domain.enumeration.AttachmentType;
import com.yalisoft.bister.domain.enumeration.AttachmentVisibilityType;
import com.yalisoft.bister.repository.AttachmentRepository;
import com.yalisoft.bister.repository.EntityManager;
import com.yalisoft.bister.repository.search.AttachmentSearchRepository;
import com.yalisoft.bister.service.AttachmentService;
import com.yalisoft.bister.service.dto.AttachmentDTO;
import com.yalisoft.bister.service.mapper.AttachmentMapper;
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
 * Integration tests for the {@link AttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AttachmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBB";

    private static final AttachmentType DEFAULT_ATTACHMENT_TYPE = AttachmentType.IMAGE;
    private static final AttachmentType UPDATED_ATTACHMENT_TYPE = AttachmentType.AUDIO;

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_APPROVAL_NEEDED = false;
    private static final Boolean UPDATED_IS_APPROVAL_NEEDED = true;

    private static final AttachmentApprovalStatus DEFAULT_APPROVAL_STATUS = AttachmentApprovalStatus.NEW;
    private static final AttachmentApprovalStatus UPDATED_APPROVAL_STATUS = AttachmentApprovalStatus.INPROGRESS;

    private static final Long DEFAULT_APPROVED_BY = 1L;
    private static final Long UPDATED_APPROVED_BY = 2L;

    private static final AttachmentSourceType DEFAULT_ATTACHMENT_SOURCE_TYPE = AttachmentSourceType.Project;
    private static final AttachmentSourceType UPDATED_ATTACHMENT_SOURCE_TYPE = AttachmentSourceType.Product;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;

    private static final Long DEFAULT_AGENT_ID = 1L;
    private static final Long UPDATED_AGENT_ID = 2L;

    private static final AttachmentVisibilityType DEFAULT_ATTACHMENT_VISIBILITY_TYPE = AttachmentVisibilityType.PRIVATE;
    private static final AttachmentVisibilityType UPDATED_ATTACHMENT_VISIBILITY_TYPE = AttachmentVisibilityType.PROTECTED;

    private static final String DEFAULT_ORIGINAL_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXTENSION = "AAAAAAAAAA";
    private static final String UPDATED_EXTENSION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SIZE_IN_BYTES = 1;
    private static final Integer UPDATED_SIZE_IN_BYTES = 2;

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/attachments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentRepository attachmentRepositoryMock;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Mock
    private AttachmentService attachmentServiceMock;

    @Autowired
    private AttachmentSearchRepository attachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Attachment attachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createEntity(EntityManager em) {
        Attachment attachment = new Attachment()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .attachmentType(DEFAULT_ATTACHMENT_TYPE)
            .link(DEFAULT_LINK)
            .isApprovalNeeded(DEFAULT_IS_APPROVAL_NEEDED)
            .approvalStatus(DEFAULT_APPROVAL_STATUS)
            .approvedBy(DEFAULT_APPROVED_BY)
            .attachmentSourceType(DEFAULT_ATTACHMENT_SOURCE_TYPE)
            .createdBy(DEFAULT_CREATED_BY)
            .customerId(DEFAULT_CUSTOMER_ID)
            .agentId(DEFAULT_AGENT_ID)
            .attachmentVisibilityType(DEFAULT_ATTACHMENT_VISIBILITY_TYPE)
            .originalFilename(DEFAULT_ORIGINAL_FILENAME)
            .extension(DEFAULT_EXTENSION)
            .sizeInBytes(DEFAULT_SIZE_IN_BYTES)
            .sha256(DEFAULT_SHA_256)
            .contentType(DEFAULT_CONTENT_TYPE);
        return attachment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createUpdatedEntity(EntityManager em) {
        Attachment attachment = new Attachment()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .attachmentType(UPDATED_ATTACHMENT_TYPE)
            .link(UPDATED_LINK)
            .isApprovalNeeded(UPDATED_IS_APPROVAL_NEEDED)
            .approvalStatus(UPDATED_APPROVAL_STATUS)
            .approvedBy(UPDATED_APPROVED_BY)
            .attachmentSourceType(UPDATED_ATTACHMENT_SOURCE_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .customerId(UPDATED_CUSTOMER_ID)
            .agentId(UPDATED_AGENT_ID)
            .attachmentVisibilityType(UPDATED_ATTACHMENT_VISIBILITY_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .extension(UPDATED_EXTENSION)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .sha256(UPDATED_SHA_256)
            .contentType(UPDATED_CONTENT_TYPE);
        return attachment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Attachment.class).block();
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
        attachmentSearchRepository.deleteAll().block();
        assertThat(attachmentSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        attachment = createEntity(em);
    }

    @Test
    void createAttachment() throws Exception {
        int databaseSizeBeforeCreate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAttachment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAttachment.getAttachmentType()).isEqualTo(DEFAULT_ATTACHMENT_TYPE);
        assertThat(testAttachment.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testAttachment.getIsApprovalNeeded()).isEqualTo(DEFAULT_IS_APPROVAL_NEEDED);
        assertThat(testAttachment.getApprovalStatus()).isEqualTo(DEFAULT_APPROVAL_STATUS);
        assertThat(testAttachment.getApprovedBy()).isEqualTo(DEFAULT_APPROVED_BY);
        assertThat(testAttachment.getAttachmentSourceType()).isEqualTo(DEFAULT_ATTACHMENT_SOURCE_TYPE);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAttachment.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testAttachment.getAgentId()).isEqualTo(DEFAULT_AGENT_ID);
        assertThat(testAttachment.getAttachmentVisibilityType()).isEqualTo(DEFAULT_ATTACHMENT_VISIBILITY_TYPE);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(DEFAULT_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(DEFAULT_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(DEFAULT_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(DEFAULT_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
    }

    @Test
    void createAttachmentWithExistingId() throws Exception {
        // Create the Attachment with an existing ID
        attachment.setId(1L);
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        int databaseSizeBeforeCreate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        // set the field null
        attachment.setName(null);

        // Create the Attachment, which fails.
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllAttachments() {
        // Initialize the database
        attachmentRepository.save(attachment).block();

        // Get all the attachmentList
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
            .value(hasItem(attachment.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].attachmentType")
            .value(hasItem(DEFAULT_ATTACHMENT_TYPE.toString()))
            .jsonPath("$.[*].link")
            .value(hasItem(DEFAULT_LINK))
            .jsonPath("$.[*].isApprovalNeeded")
            .value(hasItem(DEFAULT_IS_APPROVAL_NEEDED.booleanValue()))
            .jsonPath("$.[*].approvalStatus")
            .value(hasItem(DEFAULT_APPROVAL_STATUS.toString()))
            .jsonPath("$.[*].approvedBy")
            .value(hasItem(DEFAULT_APPROVED_BY.intValue()))
            .jsonPath("$.[*].attachmentSourceType")
            .value(hasItem(DEFAULT_ATTACHMENT_SOURCE_TYPE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].customerId")
            .value(hasItem(DEFAULT_CUSTOMER_ID.intValue()))
            .jsonPath("$.[*].agentId")
            .value(hasItem(DEFAULT_AGENT_ID.intValue()))
            .jsonPath("$.[*].attachmentVisibilityType")
            .value(hasItem(DEFAULT_ATTACHMENT_VISIBILITY_TYPE.toString()))
            .jsonPath("$.[*].originalFilename")
            .value(hasItem(DEFAULT_ORIGINAL_FILENAME))
            .jsonPath("$.[*].extension")
            .value(hasItem(DEFAULT_EXTENSION))
            .jsonPath("$.[*].sizeInBytes")
            .value(hasItem(DEFAULT_SIZE_IN_BYTES))
            .jsonPath("$.[*].sha256")
            .value(hasItem(DEFAULT_SHA_256))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAttachmentsWithEagerRelationshipsIsEnabled() {
        when(attachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(attachmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAttachmentsWithEagerRelationshipsIsNotEnabled() {
        when(attachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(attachmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAttachment() {
        // Initialize the database
        attachmentRepository.save(attachment).block();

        // Get the attachment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, attachment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(attachment.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.attachmentType")
            .value(is(DEFAULT_ATTACHMENT_TYPE.toString()))
            .jsonPath("$.link")
            .value(is(DEFAULT_LINK))
            .jsonPath("$.isApprovalNeeded")
            .value(is(DEFAULT_IS_APPROVAL_NEEDED.booleanValue()))
            .jsonPath("$.approvalStatus")
            .value(is(DEFAULT_APPROVAL_STATUS.toString()))
            .jsonPath("$.approvedBy")
            .value(is(DEFAULT_APPROVED_BY.intValue()))
            .jsonPath("$.attachmentSourceType")
            .value(is(DEFAULT_ATTACHMENT_SOURCE_TYPE.toString()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.customerId")
            .value(is(DEFAULT_CUSTOMER_ID.intValue()))
            .jsonPath("$.agentId")
            .value(is(DEFAULT_AGENT_ID.intValue()))
            .jsonPath("$.attachmentVisibilityType")
            .value(is(DEFAULT_ATTACHMENT_VISIBILITY_TYPE.toString()))
            .jsonPath("$.originalFilename")
            .value(is(DEFAULT_ORIGINAL_FILENAME))
            .jsonPath("$.extension")
            .value(is(DEFAULT_EXTENSION))
            .jsonPath("$.sizeInBytes")
            .value(is(DEFAULT_SIZE_IN_BYTES))
            .jsonPath("$.sha256")
            .value(is(DEFAULT_SHA_256))
            .jsonPath("$.contentType")
            .value(is(DEFAULT_CONTENT_TYPE));
    }

    @Test
    void getNonExistingAttachment() {
        // Get the attachment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAttachment() throws Exception {
        // Initialize the database
        attachmentRepository.save(attachment).block();

        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        attachmentSearchRepository.save(attachment).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());

        // Update the attachment
        Attachment updatedAttachment = attachmentRepository.findById(attachment.getId()).block();
        updatedAttachment
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .attachmentType(UPDATED_ATTACHMENT_TYPE)
            .link(UPDATED_LINK)
            .isApprovalNeeded(UPDATED_IS_APPROVAL_NEEDED)
            .approvalStatus(UPDATED_APPROVAL_STATUS)
            .approvedBy(UPDATED_APPROVED_BY)
            .attachmentSourceType(UPDATED_ATTACHMENT_SOURCE_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .customerId(UPDATED_CUSTOMER_ID)
            .agentId(UPDATED_AGENT_ID)
            .attachmentVisibilityType(UPDATED_ATTACHMENT_VISIBILITY_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .extension(UPDATED_EXTENSION)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .sha256(UPDATED_SHA_256)
            .contentType(UPDATED_CONTENT_TYPE);
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(updatedAttachment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attachmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAttachment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAttachment.getAttachmentType()).isEqualTo(UPDATED_ATTACHMENT_TYPE);
        assertThat(testAttachment.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testAttachment.getIsApprovalNeeded()).isEqualTo(UPDATED_IS_APPROVAL_NEEDED);
        assertThat(testAttachment.getApprovalStatus()).isEqualTo(UPDATED_APPROVAL_STATUS);
        assertThat(testAttachment.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
        assertThat(testAttachment.getAttachmentSourceType()).isEqualTo(UPDATED_ATTACHMENT_SOURCE_TYPE);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAttachment.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testAttachment.getAgentId()).isEqualTo(UPDATED_AGENT_ID);
        assertThat(testAttachment.getAttachmentVisibilityType()).isEqualTo(UPDATED_ATTACHMENT_VISIBILITY_TYPE);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(UPDATED_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(UPDATED_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(UPDATED_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Attachment> attachmentSearchList = IterableUtils.toList(attachmentSearchRepository.findAll().collectList().block());
                Attachment testAttachmentSearch = attachmentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAttachmentSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testAttachmentSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testAttachmentSearch.getAttachmentType()).isEqualTo(UPDATED_ATTACHMENT_TYPE);
                assertThat(testAttachmentSearch.getLink()).isEqualTo(UPDATED_LINK);
                assertThat(testAttachmentSearch.getIsApprovalNeeded()).isEqualTo(UPDATED_IS_APPROVAL_NEEDED);
                assertThat(testAttachmentSearch.getApprovalStatus()).isEqualTo(UPDATED_APPROVAL_STATUS);
                assertThat(testAttachmentSearch.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
                assertThat(testAttachmentSearch.getAttachmentSourceType()).isEqualTo(UPDATED_ATTACHMENT_SOURCE_TYPE);
                assertThat(testAttachmentSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
                assertThat(testAttachmentSearch.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
                assertThat(testAttachmentSearch.getAgentId()).isEqualTo(UPDATED_AGENT_ID);
                assertThat(testAttachmentSearch.getAttachmentVisibilityType()).isEqualTo(UPDATED_ATTACHMENT_VISIBILITY_TYPE);
                assertThat(testAttachmentSearch.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
                assertThat(testAttachmentSearch.getExtension()).isEqualTo(UPDATED_EXTENSION);
                assertThat(testAttachmentSearch.getSizeInBytes()).isEqualTo(UPDATED_SIZE_IN_BYTES);
                assertThat(testAttachmentSearch.getSha256()).isEqualTo(UPDATED_SHA_256);
                assertThat(testAttachmentSearch.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
            });
    }

    @Test
    void putNonExistingAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attachmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateAttachmentWithPatch() throws Exception {
        // Initialize the database
        attachmentRepository.save(attachment).block();

        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();

        // Update the attachment using partial update
        Attachment partialUpdatedAttachment = new Attachment();
        partialUpdatedAttachment.setId(attachment.getId());

        partialUpdatedAttachment
            .description(UPDATED_DESCRIPTION)
            .link(UPDATED_LINK)
            .approvedBy(UPDATED_APPROVED_BY)
            .attachmentSourceType(UPDATED_ATTACHMENT_SOURCE_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .contentType(UPDATED_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttachment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAttachment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAttachment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAttachment.getAttachmentType()).isEqualTo(DEFAULT_ATTACHMENT_TYPE);
        assertThat(testAttachment.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testAttachment.getIsApprovalNeeded()).isEqualTo(DEFAULT_IS_APPROVAL_NEEDED);
        assertThat(testAttachment.getApprovalStatus()).isEqualTo(DEFAULT_APPROVAL_STATUS);
        assertThat(testAttachment.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
        assertThat(testAttachment.getAttachmentSourceType()).isEqualTo(UPDATED_ATTACHMENT_SOURCE_TYPE);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAttachment.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testAttachment.getAgentId()).isEqualTo(DEFAULT_AGENT_ID);
        assertThat(testAttachment.getAttachmentVisibilityType()).isEqualTo(DEFAULT_ATTACHMENT_VISIBILITY_TYPE);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(DEFAULT_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(UPDATED_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(DEFAULT_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
    }

    @Test
    void fullUpdateAttachmentWithPatch() throws Exception {
        // Initialize the database
        attachmentRepository.save(attachment).block();

        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();

        // Update the attachment using partial update
        Attachment partialUpdatedAttachment = new Attachment();
        partialUpdatedAttachment.setId(attachment.getId());

        partialUpdatedAttachment
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .attachmentType(UPDATED_ATTACHMENT_TYPE)
            .link(UPDATED_LINK)
            .isApprovalNeeded(UPDATED_IS_APPROVAL_NEEDED)
            .approvalStatus(UPDATED_APPROVAL_STATUS)
            .approvedBy(UPDATED_APPROVED_BY)
            .attachmentSourceType(UPDATED_ATTACHMENT_SOURCE_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .customerId(UPDATED_CUSTOMER_ID)
            .agentId(UPDATED_AGENT_ID)
            .attachmentVisibilityType(UPDATED_ATTACHMENT_VISIBILITY_TYPE)
            .originalFilename(UPDATED_ORIGINAL_FILENAME)
            .extension(UPDATED_EXTENSION)
            .sizeInBytes(UPDATED_SIZE_IN_BYTES)
            .sha256(UPDATED_SHA_256)
            .contentType(UPDATED_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttachment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAttachment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        Attachment testAttachment = attachmentList.get(attachmentList.size() - 1);
        assertThat(testAttachment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAttachment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAttachment.getAttachmentType()).isEqualTo(UPDATED_ATTACHMENT_TYPE);
        assertThat(testAttachment.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testAttachment.getIsApprovalNeeded()).isEqualTo(UPDATED_IS_APPROVAL_NEEDED);
        assertThat(testAttachment.getApprovalStatus()).isEqualTo(UPDATED_APPROVAL_STATUS);
        assertThat(testAttachment.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
        assertThat(testAttachment.getAttachmentSourceType()).isEqualTo(UPDATED_ATTACHMENT_SOURCE_TYPE);
        assertThat(testAttachment.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAttachment.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testAttachment.getAgentId()).isEqualTo(UPDATED_AGENT_ID);
        assertThat(testAttachment.getAttachmentVisibilityType()).isEqualTo(UPDATED_ATTACHMENT_VISIBILITY_TYPE);
        assertThat(testAttachment.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
        assertThat(testAttachment.getExtension()).isEqualTo(UPDATED_EXTENSION);
        assertThat(testAttachment.getSizeInBytes()).isEqualTo(UPDATED_SIZE_IN_BYTES);
        assertThat(testAttachment.getSha256()).isEqualTo(UPDATED_SHA_256);
        assertThat(testAttachment.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, attachmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamAttachment() throws Exception {
        int databaseSizeBeforeUpdate = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        attachment.setId(count.incrementAndGet());

        // Create the Attachment
        AttachmentDTO attachmentDTO = attachmentMapper.toDto(attachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(attachmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Attachment in the database
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteAttachment() {
        // Initialize the database
        attachmentRepository.save(attachment).block();
        attachmentRepository.save(attachment).block();
        attachmentSearchRepository.save(attachment).block();

        int databaseSizeBeforeDelete = attachmentRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the attachment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, attachment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Attachment> attachmentList = attachmentRepository.findAll().collectList().block();
        assertThat(attachmentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(attachmentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchAttachment() {
        // Initialize the database
        attachment = attachmentRepository.save(attachment).block();
        attachmentSearchRepository.save(attachment).block();

        // Search the attachment
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + attachment.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(attachment.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].attachmentType")
            .value(hasItem(DEFAULT_ATTACHMENT_TYPE.toString()))
            .jsonPath("$.[*].link")
            .value(hasItem(DEFAULT_LINK))
            .jsonPath("$.[*].isApprovalNeeded")
            .value(hasItem(DEFAULT_IS_APPROVAL_NEEDED.booleanValue()))
            .jsonPath("$.[*].approvalStatus")
            .value(hasItem(DEFAULT_APPROVAL_STATUS.toString()))
            .jsonPath("$.[*].approvedBy")
            .value(hasItem(DEFAULT_APPROVED_BY.intValue()))
            .jsonPath("$.[*].attachmentSourceType")
            .value(hasItem(DEFAULT_ATTACHMENT_SOURCE_TYPE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY.intValue()))
            .jsonPath("$.[*].customerId")
            .value(hasItem(DEFAULT_CUSTOMER_ID.intValue()))
            .jsonPath("$.[*].agentId")
            .value(hasItem(DEFAULT_AGENT_ID.intValue()))
            .jsonPath("$.[*].attachmentVisibilityType")
            .value(hasItem(DEFAULT_ATTACHMENT_VISIBILITY_TYPE.toString()))
            .jsonPath("$.[*].originalFilename")
            .value(hasItem(DEFAULT_ORIGINAL_FILENAME))
            .jsonPath("$.[*].extension")
            .value(hasItem(DEFAULT_EXTENSION))
            .jsonPath("$.[*].sizeInBytes")
            .value(hasItem(DEFAULT_SIZE_IN_BYTES))
            .jsonPath("$.[*].sha256")
            .value(hasItem(DEFAULT_SHA_256))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE));
    }
}
