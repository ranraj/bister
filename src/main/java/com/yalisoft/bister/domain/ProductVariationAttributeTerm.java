package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductVariationAttributeTerm.
 */
@Table("product_variation_attribute_term")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productvariationattributeterm")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariationAttributeTerm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("slug")
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 1000)
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("menu_order")
    private Integer menuOrder;

    @Column("over_ride_product_attribute")
    private Boolean overRideProductAttribute;

    @Transient
    @JsonIgnoreProperties(value = { "productVariationAttributeTerms", "product" }, allowSetters = true)
    private ProductVariation productVariation;

    @Column("product_variation_id")
    private Long productVariationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductVariationAttributeTerm id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProductVariationAttributeTerm name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public ProductVariationAttributeTerm slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductVariationAttributeTerm description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMenuOrder() {
        return this.menuOrder;
    }

    public ProductVariationAttributeTerm menuOrder(Integer menuOrder) {
        this.setMenuOrder(menuOrder);
        return this;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public Boolean getOverRideProductAttribute() {
        return this.overRideProductAttribute;
    }

    public ProductVariationAttributeTerm overRideProductAttribute(Boolean overRideProductAttribute) {
        this.setOverRideProductAttribute(overRideProductAttribute);
        return this;
    }

    public void setOverRideProductAttribute(Boolean overRideProductAttribute) {
        this.overRideProductAttribute = overRideProductAttribute;
    }

    public ProductVariation getProductVariation() {
        return this.productVariation;
    }

    public void setProductVariation(ProductVariation productVariation) {
        this.productVariation = productVariation;
        this.productVariationId = productVariation != null ? productVariation.getId() : null;
    }

    public ProductVariationAttributeTerm productVariation(ProductVariation productVariation) {
        this.setProductVariation(productVariation);
        return this;
    }

    public Long getProductVariationId() {
        return this.productVariationId;
    }

    public void setProductVariationId(Long productVariation) {
        this.productVariationId = productVariation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariationAttributeTerm)) {
            return false;
        }
        return id != null && id.equals(((ProductVariationAttributeTerm) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariationAttributeTerm{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", menuOrder=" + getMenuOrder() +
            ", overRideProductAttribute='" + getOverRideProductAttribute() + "'" +
            "}";
    }
}
