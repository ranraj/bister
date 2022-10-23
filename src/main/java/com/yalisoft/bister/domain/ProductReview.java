package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductReview.
 */
@Table("product_review")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productreview")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    @Column("reviewer_name")
    private String reviewerName;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("reviewer_email")
    private String reviewerEmail;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 1000)
    @Column("review")
    private String review;

    @NotNull(message = "must not be null")
    @Column("rating")
    private Integer rating;

    @NotNull(message = "must not be null")
    @Column("status")
    private ReviewStatus status;

    @NotNull(message = "must not be null")
    @Column("reviewer_id")
    private Long reviewerId;

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

    public ProductReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewerName() {
        return this.reviewerName;
    }

    public ProductReview reviewerName(String reviewerName) {
        this.setReviewerName(reviewerName);
        return this;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return this.reviewerEmail;
    }

    public ProductReview reviewerEmail(String reviewerEmail) {
        this.setReviewerEmail(reviewerEmail);
        return this;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public String getReview() {
        return this.review;
    }

    public ProductReview review(String review) {
        this.setReview(review);
        return this;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return this.rating;
    }

    public ProductReview rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public ReviewStatus getStatus() {
        return this.status;
    }

    public ProductReview status(ReviewStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Long getReviewerId() {
        return this.reviewerId;
    }

    public ProductReview reviewerId(Long reviewerId) {
        this.setReviewerId(reviewerId);
        return this;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductReview product(Product product) {
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
        if (!(o instanceof ProductReview)) {
            return false;
        }
        return id != null && id.equals(((ProductReview) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductReview{" +
            "id=" + getId() +
            ", reviewerName='" + getReviewerName() + "'" +
            ", reviewerEmail='" + getReviewerEmail() + "'" +
            ", review='" + getReview() + "'" +
            ", rating=" + getRating() +
            ", status='" + getStatus() + "'" +
            ", reviewerId=" + getReviewerId() +
            "}";
    }
}
