package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProductVariationAttributeTerm} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariationAttributeTermDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 1000)
    private String description;

    @NotNull(message = "must not be null")
    private Integer menuOrder;

    private Boolean overRideProductAttribute;

    private ProductVariationDTO productVariation;

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

    public Integer getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public Boolean getOverRideProductAttribute() {
        return overRideProductAttribute;
    }

    public void setOverRideProductAttribute(Boolean overRideProductAttribute) {
        this.overRideProductAttribute = overRideProductAttribute;
    }

    public ProductVariationDTO getProductVariation() {
        return productVariation;
    }

    public void setProductVariation(ProductVariationDTO productVariation) {
        this.productVariation = productVariation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariationAttributeTermDTO)) {
            return false;
        }

        ProductVariationAttributeTermDTO productVariationAttributeTermDTO = (ProductVariationAttributeTermDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productVariationAttributeTermDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariationAttributeTermDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", menuOrder=" + getMenuOrder() +
            ", overRideProductAttribute='" + getOverRideProductAttribute() + "'" +
            ", productVariation=" + getProductVariation() +
            "}";
    }
}
