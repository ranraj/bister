package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.AttachmentApprovalStatus;
import com.yalisoft.bister.domain.enumeration.AttachmentSourceType;
import com.yalisoft.bister.domain.enumeration.AttachmentType;
import com.yalisoft.bister.domain.enumeration.AttachmentVisibilityType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Attachment.
 */
@Table("attachment")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "attachment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    @Column("name")
    private String name;

    @Size(min = 20, max = 250)
    @Column("description")
    private String description;

    @Column("attachment_type")
    private AttachmentType attachmentType;

    @Size(min = 2, max = 250)
    @Column("link")
    private String link;

    @Column("is_approval_needed")
    private Boolean isApprovalNeeded;

    @Column("approval_status")
    private AttachmentApprovalStatus approvalStatus;

    @Column("approved_by")
    private Long approvedBy;

    @Column("attachment_source_type")
    private AttachmentSourceType attachmentSourceType;

    @Column("created_by")
    private Long createdBy;

    @Column("customer_id")
    private Long customerId;

    @Column("agent_id")
    private Long agentId;

    @Column("attachment_visibility_type")
    private AttachmentVisibilityType attachmentVisibilityType;

    @Column("original_filename")
    private String originalFilename;

    @Column("extension")
    private String extension;

    @Column("size_in_bytes")
    private Integer sizeInBytes;

    @Column("sha_256")
    private String sha256;

    @Column("content_type")
    private String contentType;

    @Transient
    @JsonIgnoreProperties(value = { "product", "project", "attachment" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = {
            "productVariations",
            "productAttributeTerms",
            "tags",
            "productReviews",
            "productSpecifications",
            "attachments",
            "enquiries",
            "productActivities",
            "project",
            "categories",
            "taxClass",
        },
        allowSetters = true
    )
    private Product product;

    @Transient
    @JsonIgnoreProperties(
        value = {
            "address",
            "tags",
            "projectReviews",
            "projectSpecifications",
            "attachments",
            "enquiries",
            "projectActivities",
            "projectType",
            "categories",
        },
        allowSetters = true
    )
    private Project project;

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "enquiryResponses", "agent", "project", "product", "customer" }, allowSetters = true)
    private Enquiry enquiry;

    @Transient
    @JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
    private Certification certification;

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "productSpecificationGroup", "product" }, allowSetters = true)
    private ProductSpecification productSpecification;

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "projectSpecificationGroup", "project" }, allowSetters = true)
    private ProjectSpecification projectSpecification;

    @Column("product_id")
    private Long productId;

    @Column("project_id")
    private Long projectId;

    @Column("enquiry_id")
    private Long enquiryId;

    @Column("certification_id")
    private Long certificationId;

    @Column("product_specification_id")
    private Long productSpecificationId;

    @Column("project_specification_id")
    private Long projectSpecificationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attachment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Attachment name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Attachment description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AttachmentType getAttachmentType() {
        return this.attachmentType;
    }

    public Attachment attachmentType(AttachmentType attachmentType) {
        this.setAttachmentType(attachmentType);
        return this;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getLink() {
        return this.link;
    }

    public Attachment link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getIsApprovalNeeded() {
        return this.isApprovalNeeded;
    }

    public Attachment isApprovalNeeded(Boolean isApprovalNeeded) {
        this.setIsApprovalNeeded(isApprovalNeeded);
        return this;
    }

    public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
        this.isApprovalNeeded = isApprovalNeeded;
    }

    public AttachmentApprovalStatus getApprovalStatus() {
        return this.approvalStatus;
    }

    public Attachment approvalStatus(AttachmentApprovalStatus approvalStatus) {
        this.setApprovalStatus(approvalStatus);
        return this;
    }

    public void setApprovalStatus(AttachmentApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return this.approvedBy;
    }

    public Attachment approvedBy(Long approvedBy) {
        this.setApprovedBy(approvedBy);
        return this;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public AttachmentSourceType getAttachmentSourceType() {
        return this.attachmentSourceType;
    }

    public Attachment attachmentSourceType(AttachmentSourceType attachmentSourceType) {
        this.setAttachmentSourceType(attachmentSourceType);
        return this;
    }

    public void setAttachmentSourceType(AttachmentSourceType attachmentSourceType) {
        this.attachmentSourceType = attachmentSourceType;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Attachment createdBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public Attachment customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAgentId() {
        return this.agentId;
    }

    public Attachment agentId(Long agentId) {
        this.setAgentId(agentId);
        return this;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public AttachmentVisibilityType getAttachmentVisibilityType() {
        return this.attachmentVisibilityType;
    }

    public Attachment attachmentVisibilityType(AttachmentVisibilityType attachmentVisibilityType) {
        this.setAttachmentVisibilityType(attachmentVisibilityType);
        return this;
    }

    public void setAttachmentVisibilityType(AttachmentVisibilityType attachmentVisibilityType) {
        this.attachmentVisibilityType = attachmentVisibilityType;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public Attachment originalFilename(String originalFilename) {
        this.setOriginalFilename(originalFilename);
        return this;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getExtension() {
        return this.extension;
    }

    public Attachment extension(String extension) {
        this.setExtension(extension);
        return this;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Integer getSizeInBytes() {
        return this.sizeInBytes;
    }

    public Attachment sizeInBytes(Integer sizeInBytes) {
        this.setSizeInBytes(sizeInBytes);
        return this;
    }

    public void setSizeInBytes(Integer sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getSha256() {
        return this.sha256;
    }

    public Attachment sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Attachment contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setAttachment(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setAttachment(this));
        }
        this.tags = tags;
    }

    public Attachment tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Attachment addTag(Tag tag) {
        this.tags.add(tag);
        tag.setAttachment(this);
        return this;
    }

    public Attachment removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.setAttachment(null);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public Attachment product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Attachment project(Project project) {
        this.setProject(project);
        return this;
    }

    public Enquiry getEnquiry() {
        return this.enquiry;
    }

    public void setEnquiry(Enquiry enquiry) {
        this.enquiry = enquiry;
        this.enquiryId = enquiry != null ? enquiry.getId() : null;
    }

    public Attachment enquiry(Enquiry enquiry) {
        this.setEnquiry(enquiry);
        return this;
    }

    public Certification getCertification() {
        return this.certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
        this.certificationId = certification != null ? certification.getId() : null;
    }

    public Attachment certification(Certification certification) {
        this.setCertification(certification);
        return this;
    }

    public ProductSpecification getProductSpecification() {
        return this.productSpecification;
    }

    public void setProductSpecification(ProductSpecification productSpecification) {
        this.productSpecification = productSpecification;
        this.productSpecificationId = productSpecification != null ? productSpecification.getId() : null;
    }

    public Attachment productSpecification(ProductSpecification productSpecification) {
        this.setProductSpecification(productSpecification);
        return this;
    }

    public ProjectSpecification getProjectSpecification() {
        return this.projectSpecification;
    }

    public void setProjectSpecification(ProjectSpecification projectSpecification) {
        this.projectSpecification = projectSpecification;
        this.projectSpecificationId = projectSpecification != null ? projectSpecification.getId() : null;
    }

    public Attachment projectSpecification(ProjectSpecification projectSpecification) {
        this.setProjectSpecification(projectSpecification);
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    public Long getEnquiryId() {
        return this.enquiryId;
    }

    public void setEnquiryId(Long enquiry) {
        this.enquiryId = enquiry;
    }

    public Long getCertificationId() {
        return this.certificationId;
    }

    public void setCertificationId(Long certification) {
        this.certificationId = certification;
    }

    public Long getProductSpecificationId() {
        return this.productSpecificationId;
    }

    public void setProductSpecificationId(Long productSpecification) {
        this.productSpecificationId = productSpecification;
    }

    public Long getProjectSpecificationId() {
        return this.projectSpecificationId;
    }

    public void setProjectSpecificationId(Long projectSpecification) {
        this.projectSpecificationId = projectSpecification;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return id != null && id.equals(((Attachment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attachment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", attachmentType='" + getAttachmentType() + "'" +
            ", link='" + getLink() + "'" +
            ", isApprovalNeeded='" + getIsApprovalNeeded() + "'" +
            ", approvalStatus='" + getApprovalStatus() + "'" +
            ", approvedBy=" + getApprovedBy() +
            ", attachmentSourceType='" + getAttachmentSourceType() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", customerId=" + getCustomerId() +
            ", agentId=" + getAgentId() +
            ", attachmentVisibilityType='" + getAttachmentVisibilityType() + "'" +
            ", originalFilename='" + getOriginalFilename() + "'" +
            ", extension='" + getExtension() + "'" +
            ", sizeInBytes=" + getSizeInBytes() +
            ", sha256='" + getSha256() + "'" +
            ", contentType='" + getContentType() + "'" +
            "}";
    }
}
