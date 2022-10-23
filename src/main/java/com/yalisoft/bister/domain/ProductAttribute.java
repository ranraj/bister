package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductAttribute.
 */
@Table("product_attribute")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productattribute")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductAttribute implements Serializable {

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
    @Column("type")
    private String type;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 1000)
    @Column("notes")
    private String notes;

    @Column("visible")
    private Boolean visible;

    @Transient
    @JsonIgnoreProperties(value = { "productAttribute", "product" }, allowSetters = true)
    private Set<ProductAttributeTerm> productAttributeTerms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProductAttribute name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public ProductAttribute slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return this.type;
    }

    public ProductAttribute type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return this.notes;
    }

    public ProductAttribute notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public ProductAttribute visible(Boolean visible) {
        this.setVisible(visible);
        return this;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Set<ProductAttributeTerm> getProductAttributeTerms() {
        return this.productAttributeTerms;
    }

    public void setProductAttributeTerms(Set<ProductAttributeTerm> productAttributeTerms) {
        if (this.productAttributeTerms != null) {
            this.productAttributeTerms.forEach(i -> i.setProductAttribute(null));
        }
        if (productAttributeTerms != null) {
            productAttributeTerms.forEach(i -> i.setProductAttribute(this));
        }
        this.productAttributeTerms = productAttributeTerms;
    }

    public ProductAttribute productAttributeTerms(Set<ProductAttributeTerm> productAttributeTerms) {
        this.setProductAttributeTerms(productAttributeTerms);
        return this;
    }

    public ProductAttribute addProductAttributeTerm(ProductAttributeTerm productAttributeTerm) {
        this.productAttributeTerms.add(productAttributeTerm);
        productAttributeTerm.setProductAttribute(this);
        return this;
    }

    public ProductAttribute removeProductAttributeTerm(ProductAttributeTerm productAttributeTerm) {
        this.productAttributeTerms.remove(productAttributeTerm);
        productAttributeTerm.setProductAttribute(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAttribute)) {
            return false;
        }
        return id != null && id.equals(((ProductAttribute) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductAttribute{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", type='" + getType() + "'" +
            ", notes='" + getNotes() + "'" +
            ", visible='" + getVisible() + "'" +
            "}";
    }
}
