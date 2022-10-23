package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProductSpecificationGroup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductSpecificationGroupDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String slug;

    @Size(min = 20, max = 250)
    private String description;

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
        if (!(o instanceof ProductSpecificationGroupDTO)) {
            return false;
        }

        ProductSpecificationGroupDTO productSpecificationGroupDTO = (ProductSpecificationGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productSpecificationGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductSpecificationGroupDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
