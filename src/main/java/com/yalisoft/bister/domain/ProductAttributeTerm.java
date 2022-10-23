package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductAttributeTerm.
 */
@Table("product_attribute_term")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productattributeterm")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductAttributeTerm implements Serializable {

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

    @Transient
    @JsonIgnoreProperties(value = { "productAttributeTerms" }, allowSetters = true)
    private ProductAttribute productAttribute;

    @Transient
    @JsonIgnoreProperties(
        value = {
            "productVariations",
            "productAttributeTerms",
            "tags",
            "productReviews",
            "productSpecifications",
            "attachments",
            "enquiries",
            "productActivities",
            "project",
            "categories",
            "taxClass",
        },
        allowSetters = true
    )
    private Product product;

    @Column("product_attribute_id")
    private Long productAttributeId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductAttributeTerm id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProductAttributeTerm name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public ProductAttributeTerm slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductAttributeTerm description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMenuOrder() {
        return this.menuOrder;
    }

    public ProductAttributeTerm menuOrder(Integer menuOrder) {
        this.setMenuOrder(menuOrder);
        return this;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public ProductAttribute getProductAttribute() {
        return this.productAttribute;
    }

    public void setProductAttribute(ProductAttribute productAttribute) {
        this.productAttribute = productAttribute;
        this.productAttributeId = productAttribute != null ? productAttribute.getId() : null;
    }

    public ProductAttributeTerm productAttribute(ProductAttribute productAttribute) {
        this.setProductAttribute(productAttribute);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductAttributeTerm product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getProductAttributeId() {
        return this.productAttributeId;
    }

    public void setProductAttributeId(Long productAttribute) {
        this.productAttributeId = productAttribute;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAttributeTerm)) {
            return false;
        }
        return id != null && id.equals(((ProductAttributeTerm) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductAttributeTerm{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", menuOrder=" + getMenuOrder() +
            "}";
    }
}
