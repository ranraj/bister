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
 * A TaxClass.
 */
@Table("tax_class")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "taxclass")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    @Column("slug")
    private String slug;

    @Transient
    @JsonIgnoreProperties(value = { "taxClass" }, allowSetters = true)
    private Set<TaxRate> taxRates = new HashSet<>();

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
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaxClass id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TaxClass name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public TaxClass slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Set<TaxRate> getTaxRates() {
        return this.taxRates;
    }

    public void setTaxRates(Set<TaxRate> taxRates) {
        if (this.taxRates != null) {
            this.taxRates.forEach(i -> i.setTaxClass(null));
        }
        if (taxRates != null) {
            taxRates.forEach(i -> i.setTaxClass(this));
        }
        this.taxRates = taxRates;
    }

    public TaxClass taxRates(Set<TaxRate> taxRates) {
        this.setTaxRates(taxRates);
        return this;
    }

    public TaxClass addTaxRate(TaxRate taxRate) {
        this.taxRates.add(taxRate);
        taxRate.setTaxClass(this);
        return this;
    }

    public TaxClass removeTaxRate(TaxRate taxRate) {
        this.taxRates.remove(taxRate);
        taxRate.setTaxClass(null);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setTaxClass(null));
        }
        if (products != null) {
            products.forEach(i -> i.setTaxClass(this));
        }
        this.products = products;
    }

    public TaxClass products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public TaxClass addProduct(Product product) {
        this.products.add(product);
        product.setTaxClass(this);
        return this;
    }

    public TaxClass removeProduct(Product product) {
        this.products.remove(product);
        product.setTaxClass(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxClass)) {
            return false;
        }
        return id != null && id.equals(((TaxClass) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxClass{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            "}";
    }
}
