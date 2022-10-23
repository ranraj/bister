package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.AttachmentApprovalStatus;
import com.yalisoft.bister.domain.enumeration.AttachmentSourceType;
import com.yalisoft.bister.domain.enumeration.AttachmentType;
import com.yalisoft.bister.domain.enumeration.AttachmentVisibilityType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Attachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttachmentDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String name;

    @Size(min = 20, max = 250)
    private String description;

    private AttachmentType attachmentType;

    @Size(min = 2, max = 250)
    private String link;

    private Boolean isApprovalNeeded;

    private AttachmentApprovalStatus approvalStatus;

    private Long approvedBy;

    private AttachmentSourceType attachmentSourceType;

    private Long createdBy;

    private Long customerId;

    private Long agentId;

    private AttachmentVisibilityType attachmentVisibilityType;

    private String originalFilename;

    private String extension;

    private Integer sizeInBytes;

    private String sha256;

    private String contentType;

    private ProductDTO product;

    private ProjectDTO project;

    private EnquiryDTO enquiry;

    private CertificationDTO certification;

    private ProductSpecificationDTO productSpecification;

    private ProjectSpecificationDTO projectSpecification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getIsApprovalNeeded() {
        return isApprovalNeeded;
    }

    public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
        this.isApprovalNeeded = isApprovalNeeded;
    }

    public AttachmentApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(AttachmentApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public AttachmentSourceType getAttachmentSourceType() {
        return attachmentSourceType;
    }

    public void setAttachmentSourceType(AttachmentSourceType attachmentSourceType) {
        this.attachmentSourceType = attachmentSourceType;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public AttachmentVisibilityType getAttachmentVisibilityType() {
        return attachmentVisibilityType;
    }

    public void setAttachmentVisibilityType(AttachmentVisibilityType attachmentVisibilityType) {
        this.attachmentVisibilityType = attachmentVisibilityType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Integer getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Integer sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public EnquiryDTO getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(EnquiryDTO enquiry) {
        this.enquiry = enquiry;
    }

    public CertificationDTO getCertification() {
        return certification;
    }

    public void setCertification(CertificationDTO certification) {
        this.certification = certification;
    }

    public ProductSpecificationDTO getProductSpecification() {
        return productSpecification;
    }

    public void setProductSpecification(ProductSpecificationDTO productSpecification) {
        this.productSpecification = productSpecification;
    }

    public ProjectSpecificationDTO getProjectSpecification() {
        return projectSpecification;
    }

    public void setProjectSpecification(ProjectSpecificationDTO projectSpecification) {
        this.projectSpecification = projectSpecification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttachmentDTO)) {
            return false;
        }

        AttachmentDTO attachmentDTO = (AttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttachmentDTO{" +
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
            ", product=" + getProduct() +
            ", project=" + getProject() +
            ", enquiry=" + getEnquiry() +
            ", certification=" + getCertification() +
            ", productSpecification=" + getProductSpecification() +
            ", projectSpecification=" + getProjectSpecification() +
            "}";
    }
}
