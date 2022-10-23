package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.SaleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDTO implements Serializable {

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

    @NotNull(message = "must not be null")
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
    private Boolean featured;

    @NotNull(message = "must not be null")
    private SaleStatus saleStatus;

    private String sharableHash;

    private ProjectDTO project;

    private Set<CategoryDTO> categories = new HashSet<>();

    private TaxClassDTO taxClass;

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

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public SaleStatus getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getSharableHash() {
        return sharableHash;
    }

    public void setSharableHash(String sharableHash) {
        this.sharableHash = sharableHash;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }

    public TaxClassDTO getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(TaxClassDTO taxClass) {
        this.taxClass = taxClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
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
            ", featured='" + getFeatured() + "'" +
            ", saleStatus='" + getSaleStatus() + "'" +
            ", sharableHash='" + getSharableHash() + "'" +
            ", project=" + getProject() +
            ", categories=" + getCategories() +
            ", taxClass=" + getTaxClass() +
            "}";
    }
}
