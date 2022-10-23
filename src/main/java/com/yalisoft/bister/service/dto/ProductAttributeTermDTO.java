package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProductAttributeTerm} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductAttributeTermDTO implements Serializable {

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

    private ProductAttributeDTO productAttribute;

    private ProductDTO product;

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

    public ProductAttributeDTO getProductAttribute() {
        return productAttribute;
    }

    public void setProductAttribute(ProductAttributeDTO productAttribute) {
        this.productAttribute = productAttribute;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAttributeTermDTO)) {
            return false;
        }

        ProductAttributeTermDTO productAttributeTermDTO = (ProductAttributeTermDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productAttributeTermDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductAttributeTermDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", menuOrder=" + getMenuOrder() +
            ", productAttribute=" + getProductAttribute() +
            ", product=" + getProduct() +
            "}";
    }
}
