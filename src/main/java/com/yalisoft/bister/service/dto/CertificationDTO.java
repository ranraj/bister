package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.CertificationStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Certification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String name;

    @Size(min = 2, max = 100)
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String authority;

    @NotNull(message = "must not be null")
    private CertificationStatus status;

    private Long projectId;

    private Long prodcut;

    private Long orgId;

    private Long facitlityId;

    @NotNull(message = "must not be null")
    private Long createdBy;

    @NotNull(message = "must not be null")
    private Instant createdAt;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public CertificationStatus getStatus() {
        return status;
    }

    public void setStatus(CertificationStatus status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProdcut() {
        return prodcut;
    }

    public void setProdcut(Long prodcut) {
        this.prodcut = prodcut;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getFacitlityId() {
        return facitlityId;
    }

    public void setFacitlityId(Long facitlityId) {
        this.facitlityId = facitlityId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificationDTO)) {
            return false;
        }

        CertificationDTO certificationDTO = (CertificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, certificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificationDTO{" +
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
