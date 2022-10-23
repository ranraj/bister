package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.ProjectStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Project} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 250)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 20, max = 1000)
    private String description;

    @Size(min = 20, max = 50)
    private String shortDescription;

    @NotNull(message = "must not be null")
    private BigDecimal regularPrice;

    @NotNull(message = "must not be null")
    private BigDecimal salePrice;

    @NotNull(message = "must not be null")
    private Boolean published;

    @NotNull(message = "must not be null")
    private Instant dateCreated;

    @NotNull(message = "must not be null")
    private LocalDate dateModified;

    @NotNull(message = "must not be null")
    private ProjectStatus projectStatus;

    private String sharableHash;

    @NotNull(message = "must not be null")
    private BigDecimal estimatedBudget;

    private AddressDTO address;

    private ProjectTypeDTO projectType;

    private Set<CategoryDTO> categories = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getSharableHash() {
        return sharableHash;
    }

    public void setSharableHash(String sharableHash) {
        this.sharableHash = sharableHash;
    }

    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public ProjectTypeDTO getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectTypeDTO projectType) {
        this.projectType = projectType;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectDTO)) {
            return false;
        }

        ProjectDTO projectDTO = (ProjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", regularPrice=" + getRegularPrice() +
            ", salePrice=" + getSalePrice() +
            ", published='" + getPublished() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            ", projectStatus='" + getProjectStatus() + "'" +
            ", sharableHash='" + getSharableHash() + "'" +
            ", estimatedBudget=" + getEstimatedBudget() +
            ", address=" + getAddress() +
            ", projectType=" + getProjectType() +
            ", categories=" + getCategories() +
            "}";
    }
}
