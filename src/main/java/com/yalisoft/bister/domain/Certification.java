package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.CertificationStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Certification.
 */
@Table("certification")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "certification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Certification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("name")
    private String name;

    @Size(min = 2, max = 100)
    @Column("slug")
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    @Column("authority")
    private String authority;

    @NotNull(message = "must not be null")
    @Column("status")
    private CertificationStatus status;

    @Column("project_id")
    private Long projectId;

    @Column("prodcut")
    private Long prodcut;

    @Column("org_id")
    private Long orgId;

    @Column("facitlity_id")
    private Long facitlityId;

    @NotNull(message = "must not be null")
    @Column("created_by")
    private Long createdBy;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @Transient
    @JsonIgnoreProperties(
        value = { "tags", "product", "project", "enquiry", "certification", "productSpecification", "projectSpecification" },
        allowSetters = true
    )
    private Set<Attachment> attachments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Certification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Certification name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Certification slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAuthority() {
        return this.authority;
    }

    public Certification authority(String authority) {
        this.setAuthority(authority);
        return this;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public CertificationStatus getStatus() {
        return this.status;
    }

    public Certification status(CertificationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CertificationStatus status) {
        this.status = status;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public Certification projectId(Long projectId) {
        this.setProjectId(projectId);
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProdcut() {
        return this.prodcut;
    }

    public Certification prodcut(Long prodcut) {
        this.setProdcut(prodcut);
        return this;
    }

    public void setProdcut(Long prodcut) {
        this.prodcut = prodcut;
    }

    public Long getOrgId() {
        return this.orgId;
    }

    public Certification orgId(Long orgId) {
        this.setOrgId(orgId);
        return this;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getFacitlityId() {
        return this.facitlityId;
    }

    public Certification facitlityId(Long facitlityId) {
        this.setFacitlityId(facitlityId);
        return this;
    }

    public void setFacitlityId(Long facitlityId) {
        this.facitlityId = facitlityId;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Certification createdBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Certification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setCertification(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setCertification(this));
        }
        this.attachments = attachments;
    }

    public Certification attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public Certification addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setCertification(this);
        return this;
    }

    public Certification removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setCertification(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certification)) {
            return false;
        }
        return id != null && id.equals(((Certification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certification{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", authority='" + getAuthority() + "'" +
            ", status='" + getStatus() + "'" +
            ", projectId=" + getProjectId() +
            ", prodcut=" + getProdcut() +
            ", orgId=" + getOrgId() +
            ", facitlityId=" + getFacitlityId() +
            ", createdBy=" + getCreatedBy() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
