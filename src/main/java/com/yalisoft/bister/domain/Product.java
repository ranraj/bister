package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.SaleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Product.
 */
@Table("product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 250)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    @Column("slug")
    private String slug;

    @NotNull(message = "must not be null")
    @Size(min = 20, max = 1000)
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Size(min = 20, max = 50)
    @Column("short_description")
    private String shortDescription;

    @NotNull(message = "must not be null")
    @Column("regular_price")
    private BigDecimal regularPrice;

    @NotNull(message = "must not be null")
    @Column("sale_price")
    private BigDecimal salePrice;

    @NotNull(message = "must not be null")
    @Column("published")
    private Boolean published;

    @NotNull(message = "must not be null")
    @Column("date_created")
    private Instant dateCreated;

    @NotNull(message = "must not be null")
    @Column("date_modified")
    private LocalDate dateModified;

    @NotNull(message = "must not be null")
    @Column("featured")
    private Boolean featured;

    @NotNull(message = "must not be null")
    @Column("sale_status")
    private SaleStatus saleStatus;

    @Column("sharable_hash")
    private String sharableHash;

    @Transient
    @JsonIgnoreProperties(value = { "productVariationAttributeTerms", "product" }, allowSetters = true)
    private Set<ProductVariation> productVariations = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "productAttribute", "product" }, allowSetters = true)
    private Set<ProductAttributeTerm> productAttributeTerms = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "product", "project", "attachment" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductReview> productReviews = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "productSpecificationGroup", "product" }, allowSetters = true)
    private Set<ProductSpecification> productSpecifications = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = { "tags", "product", "project", "enquiry", "certification", "productSpecification", "projectSpecification" },
        allowSetters = true
    )
    private Set<Attachment> attachments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "enquiryResponses", "agent", "project", "product", "customer" }, allowSetters = true)
    private Set<Enquiry> enquiries = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductActivity> productActivities = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = {
            "address",
            "tags",
            "projectReviews",
            "projectSpecifications",
            "attachments",
            "enquiries",
            "projectActivities",
            "projectType",
            "categories",
        },
        allowSetters = true
    )
    private Project project;

    @Transient
    @JsonIgnoreProperties(value = { "parent", "products", "projects" }, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "taxRates", "products" }, allowSetters = true)
    private TaxClass taxClass;

    @Column("project_id")
    private Long projectId;

    @Column("tax_class_id")
    private Long taxClassId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Product slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public Product shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getRegularPrice() {
        return this.regularPrice;
    }

    public Product regularPrice(BigDecimal regularPrice) {
        this.setRegularPrice(regularPrice);
        return this;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice != null ? regularPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public Product salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice != null ? salePrice.stripTrailingZeros() : null;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public Product published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Product dateCreated(Instant dateCreated) {
        this.setDateCreated(dateCreated);
        return this;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Product dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getFeatured() {
        return this.featured;
    }

    public Product featured(Boolean featured) {
        this.setFeatured(featured);
        return this;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public SaleStatus getSaleStatus() {
        return this.saleStatus;
    }

    public Product saleStatus(SaleStatus saleStatus) {
        this.setSaleStatus(saleStatus);
        return this;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getSharableHash() {
        return this.sharableHash;
    }

    public Product sharableHash(String sharableHash) {
        this.setSharableHash(sharableHash);
        return this;
    }

    public void setSharableHash(String sharableHash) {
        this.sharableHash = sharableHash;
    }

    public Set<ProductVariation> getProductVariations() {
        return this.productVariations;
    }

    public void setProductVariations(Set<ProductVariation> productVariations) {
        if (this.productVariations != null) {
            this.productVariations.forEach(i -> i.setProduct(null));
        }
        if (productVariations != null) {
            productVariations.forEach(i -> i.setProduct(this));
        }
        this.productVariations = productVariations;
    }

    public Product productVariations(Set<ProductVariation> productVariations) {
        this.setProductVariations(productVariations);
        return this;
    }

    public Product addProductVariation(ProductVariation productVariation) {
        this.productVariations.add(productVariation);
        productVariation.setProduct(this);
        return this;
    }

    public Product removeProductVariation(ProductVariation productVariation) {
        this.productVariations.remove(productVariation);
        productVariation.setProduct(null);
        return this;
    }

    public Set<ProductAttributeTerm> getProductAttributeTerms() {
        return this.productAttributeTerms;
    }

    public void setProductAttributeTerms(Set<ProductAttributeTerm> productAttributeTerms) {
        if (this.productAttributeTerms != null) {
            this.productAttributeTerms.forEach(i -> i.setProduct(null));
        }
        if (productAttributeTerms != null) {
            productAttributeTerms.forEach(i -> i.setProduct(this));
        }
        this.productAttributeTerms = productAttributeTerms;
    }

    public Product productAttributeTerms(Set<ProductAttributeTerm> productAttributeTerms) {
        this.setProductAttributeTerms(productAttributeTerms);
        return this;
    }

    public Product addProductAttributeTerm(ProductAttributeTerm productAttributeTerm) {
        this.productAttributeTerms.add(productAttributeTerm);
        productAttributeTerm.setProduct(this);
        return this;
    }

    public Product removeProductAttributeTerm(ProductAttributeTerm productAttributeTerm) {
        this.productAttributeTerms.remove(productAttributeTerm);
        productAttributeTerm.setProduct(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setProduct(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setProduct(this));
        }
        this.tags = tags;
    }

    public Product tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Product addTag(Tag tag) {
        this.tags.add(tag);
        tag.setProduct(this);
        return this;
    }

    public Product removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.setProduct(null);
        return this;
    }

    public Set<ProductReview> getProductReviews() {
        return this.productReviews;
    }

    public void setProductReviews(Set<ProductReview> productReviews) {
        if (this.productReviews != null) {
            this.productReviews.forEach(i -> i.setProduct(null));
        }
        if (productReviews != null) {
            productReviews.forEach(i -> i.setProduct(this));
        }
        this.productReviews = productReviews;
    }

    public Product productReviews(Set<ProductReview> productReviews) {
        this.setProductReviews(productReviews);
        return this;
    }

    public Product addProductReview(ProductReview productReview) {
        this.productReviews.add(productReview);
        productReview.setProduct(this);
        return this;
    }

    public Product removeProductReview(ProductReview productReview) {
        this.productReviews.remove(productReview);
        productReview.setProduct(null);
        return this;
    }

    public Set<ProductSpecification> getProductSpecifications() {
        return this.productSpecifications;
    }

    public void setProductSpecifications(Set<ProductSpecification> productSpecifications) {
        if (this.productSpecifications != null) {
            this.productSpecifications.forEach(i -> i.setProduct(null));
        }
        if (productSpecifications != null) {
            productSpecifications.forEach(i -> i.setProduct(this));
        }
        this.productSpecifications = productSpecifications;
    }

    public Product productSpecifications(Set<ProductSpecification> productSpecifications) {
        this.setProductSpecifications(productSpecifications);
        return this;
    }

    public Product addProductSpecification(ProductSpecification productSpecification) {
        this.productSpecifications.add(productSpecification);
        productSpecification.setProduct(this);
        return this;
    }

    public Product removeProductSpecification(ProductSpecification productSpecification) {
        this.productSpecifications.remove(productSpecification);
        productSpecification.setProduct(null);
        return this;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setProduct(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setProduct(this));
        }
        this.attachments = attachments;
    }

    public Product attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public Product addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setProduct(this);
        return this;
    }

    public Product removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setProduct(null);
        return this;
    }

    public Set<Enquiry> getEnquiries() {
        return this.enquiries;
    }

    public void setEnquiries(Set<Enquiry> enquiries) {
        if (this.enquiries != null) {
            this.enquiries.forEach(i -> i.setProduct(null));
        }
        if (enquiries != null) {
            enquiries.forEach(i -> i.setProduct(this));
        }
        this.enquiries = enquiries;
    }

    public Product enquiries(Set<Enquiry> enquiries) {
        this.setEnquiries(enquiries);
        return this;
    }

    public Product addEnquiry(Enquiry enquiry) {
        this.enquiries.add(enquiry);
        enquiry.setProduct(this);
        return this;
    }

    public Product removeEnquiry(Enquiry enquiry) {
        this.enquiries.remove(enquiry);
        enquiry.setProduct(null);
        return this;
    }

    public Set<ProductActivity> getProductActivities() {
        return this.productActivities;
    }

    public void setProductActivities(Set<ProductActivity> productActivities) {
        if (this.productActivities != null) {
            this.productActivities.forEach(i -> i.setProduct(null));
        }
        if (productActivities != null) {
            productActivities.forEach(i -> i.setProduct(this));
        }
        this.productActivities = productActivities;
    }

    public Product productActivities(Set<ProductActivity> productActivities) {
        this.setProductActivities(productActivities);
        return this;
    }

    public Product addProductActivity(ProductActivity productActivity) {
        this.productActivities.add(productActivity);
        productActivity.setProduct(this);
        return this;
    }

    public Product removeProductActivity(ProductActivity productActivity) {
        this.productActivities.remove(productActivity);
        productActivity.setProduct(null);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Product project(Project project) {
        this.setProject(project);
        return this;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Product categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Product addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
        return this;
    }

    public Product removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
        return this;
    }

    public TaxClass getTaxClass() {
        return this.taxClass;
    }

    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
        this.taxClassId = taxClass != null ? taxClass.getId() : null;
    }

    public Product taxClass(TaxClass taxClass) {
        this.setTaxClass(taxClass);
        return this;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    public Long getTaxClassId() {
        return this.taxClassId;
    }

    public void setTaxClassId(Long taxClass) {
        this.taxClassId = taxClass;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", regularPrice=" + getRegularPrice() +
            ", salePrice=" + getSalePrice() +
            ", published='" + getPublished() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            ", featured='" + getFeatured() + "'" +
            ", saleStatus='" + getSaleStatus() + "'" +
            ", sharableHash='" + getSharableHash() + "'" +
            "}";
    }
}
