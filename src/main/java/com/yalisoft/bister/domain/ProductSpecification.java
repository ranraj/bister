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
 * A ProductSpecification.
 */
@Table("product_specification")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productspecification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductSpecification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("value")
    private String value;

    @Size(min = 20, max = 250)
    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(
        value = { "tags", "product", "project", "enquiry", "certification", "productSpecification", "projectSpecification" },
        allowSetters = true
    )
    private Set<Attachment> attachments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private ProductSpecificationGroup productSpecificationGroup;

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

    @Column("product_specification_group_id")
    private Long productSpecificationGroupId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductSpecification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ProductSpecification title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return this.value;
    }

    public ProductSpecification value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductSpecification description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setProductSpecification(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setProductSpecification(this));
        }
        this.attachments = attachments;
    }

    public ProductSpecification attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public ProductSpecification addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setProductSpecification(this);
        return this;
    }

    public ProductSpecification removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setProductSpecification(null);
        return this;
    }

    public ProductSpecificationGroup getProductSpecificationGroup() {
        return this.productSpecificationGroup;
    }

    public void setProductSpecificationGroup(ProductSpecificationGroup productSpecificationGroup) {
        this.productSpecificationGroup = productSpecificationGroup;
        this.productSpecificationGroupId = productSpecificationGroup != null ? productSpecificationGroup.getId() : null;
    }

    public ProductSpecification productSpecificationGroup(ProductSpecificationGroup productSpecificationGroup) {
        this.setProductSpecificationGroup(productSpecificationGroup);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductSpecification product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getProductSpecificationGroupId() {
        return this.productSpecificationGroupId;
    }

    public void setProductSpecificationGroupId(Long productSpecificationGroup) {
        this.productSpecificationGroupId = productSpecificationGroup;
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
        if (!(o instanceof ProductSpecification)) {
            return false;
        }
        return id != null && id.equals(((ProductSpecification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductSpecification{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
