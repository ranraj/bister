package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductActivity.
 */
@Table("product_activity")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productactivity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    @Column("title")
    private String title;

    @Size(min = 20, max = 250)
    @Column("details")
    private String details;

    @NotNull(message = "must not be null")
    @Column("status")
    private ActivityStatus status;

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

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductActivity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ProductActivity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return this.details;
    }

    public ProductActivity details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ActivityStatus getStatus() {
        return this.status;
    }

    public ProductActivity status(ActivityStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductActivity product(Product product) {
        this.setProduct(product);
        return this;
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
        if (!(o instanceof ProductActivity)) {
            return false;
        }
        return id != null && id.equals(((ProductActivity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductActivity{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
