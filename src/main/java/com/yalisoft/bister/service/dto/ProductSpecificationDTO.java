package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProductSpecification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductSpecificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String value;

    @Size(min = 20, max = 250)
    private String description;

    private ProductSpecificationGroupDTO productSpecificationGroup;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductSpecificationGroupDTO getProductSpecificationGroup() {
        return productSpecificationGroup;
    }

    public void setProductSpecificationGroup(ProductSpecificationGroupDTO productSpecificationGroup) {
        this.productSpecificationGroup = productSpecificationGroup;
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
        if (!(o instanceof ProductSpecificationDTO)) {
            return false;
        }

        ProductSpecificationDTO productSpecificationDTO = (ProductSpecificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productSpecificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductSpecificationDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", productSpecificationGroup=" + getProductSpecificationGroup() +
            ", product=" + getProduct() +
            "}";
    }
}
