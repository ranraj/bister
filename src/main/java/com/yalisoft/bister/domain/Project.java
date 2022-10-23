package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.ProjectStatus;
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
 * A Project.
 */
@Table("project")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Project implements Serializable {

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
    @Column("project_status")
    private ProjectStatus projectStatus;

    @Column("sharable_hash")
    private String sharableHash;

    @NotNull(message = "must not be null")
    @Column("estimated_budget")
    private BigDecimal estimatedBudget;

    @Transient
    private Address address;

    @Transient
    @JsonIgnoreProperties(value = { "product", "project", "attachment" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<ProjectReview> projectReviews = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "projectSpecificationGroup", "project" }, allowSetters = true)
    private Set<ProjectSpecification> projectSpecifications = new HashSet<>();

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
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Set<ProjectActivity> projectActivities = new HashSet<>();

    @Transient
    private ProjectType projectType;

    @Transient
    @JsonIgnoreProperties(value = { "parent", "products", "projects" }, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    @Column("address_id")
    private Long addressId;

    @Column("project_type_id")
    private Long projectTypeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Project name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Project slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public Project description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public Project shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getRegularPrice() {
        return this.regularPrice;
    }

    public Project regularPrice(BigDecimal regularPrice) {
        this.setRegularPrice(regularPrice);
        return this;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice != null ? regularPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public Project salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice != null ? salePrice.stripTrailingZeros() : null;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public Project published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Project dateCreated(Instant dateCreated) {
        this.setDateCreated(dateCreated);
        return this;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Project dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public ProjectStatus getProjectStatus() {
        return this.projectStatus;
    }

    public Project projectStatus(ProjectStatus projectStatus) {
        this.setProjectStatus(projectStatus);
        return this;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getSharableHash() {
        return this.sharableHash;
    }

    public Project sharableHash(String sharableHash) {
        this.setSharableHash(sharableHash);
        return this;
    }

    public void setSharableHash(String sharableHash) {
        this.sharableHash = sharableHash;
    }

    public BigDecimal getEstimatedBudget() {
        return this.estimatedBudget;
    }

    public Project estimatedBudget(BigDecimal estimatedBudget) {
        this.setEstimatedBudget(estimatedBudget);
        return this;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget != null ? estimatedBudget.stripTrailingZeros() : null;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Project address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setProject(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setProject(this));
        }
        this.tags = tags;
    }

    public Project tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Project addTag(Tag tag) {
        this.tags.add(tag);
        tag.setProject(this);
        return this;
    }

    public Project removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.setProject(null);
        return this;
    }

    public Set<ProjectReview> getProjectReviews() {
        return this.projectReviews;
    }

    public void setProjectReviews(Set<ProjectReview> projectReviews) {
        if (this.projectReviews != null) {
            this.projectReviews.forEach(i -> i.setProject(null));
        }
        if (projectReviews != null) {
            projectReviews.forEach(i -> i.setProject(this));
        }
        this.projectReviews = projectReviews;
    }

    public Project projectReviews(Set<ProjectReview> projectReviews) {
        this.setProjectReviews(projectReviews);
        return this;
    }

    public Project addProjectReview(ProjectReview projectReview) {
        this.projectReviews.add(projectReview);
        projectReview.setProject(this);
        return this;
    }

    public Project removeProjectReview(ProjectReview projectReview) {
        this.projectReviews.remove(projectReview);
        projectReview.setProject(null);
        return this;
    }

    public Set<ProjectSpecification> getProjectSpecifications() {
        return this.projectSpecifications;
    }

    public void setProjectSpecifications(Set<ProjectSpecification> projectSpecifications) {
        if (this.projectSpecifications != null) {
            this.projectSpecifications.forEach(i -> i.setProject(null));
        }
        if (projectSpecifications != null) {
            projectSpecifications.forEach(i -> i.setProject(this));
        }
        this.projectSpecifications = projectSpecifications;
    }

    public Project projectSpecifications(Set<ProjectSpecification> projectSpecifications) {
        this.setProjectSpecifications(projectSpecifications);
        return this;
    }

    public Project addProjectSpecification(ProjectSpecification projectSpecification) {
        this.projectSpecifications.add(projectSpecification);
        projectSpecification.setProject(this);
        return this;
    }

    public Project removeProjectSpecification(ProjectSpecification projectSpecification) {
        this.projectSpecifications.remove(projectSpecification);
        projectSpecification.setProject(null);
        return this;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setProject(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setProject(this));
        }
        this.attachments = attachments;
    }

    public Project attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public Project addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setProject(this);
        return this;
    }

    public Project removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setProject(null);
        return this;
    }

    public Set<Enquiry> getEnquiries() {
        return this.enquiries;
    }

    public void setEnquiries(Set<Enquiry> enquiries) {
        if (this.enquiries != null) {
            this.enquiries.forEach(i -> i.setProject(null));
        }
        if (enquiries != null) {
            enquiries.forEach(i -> i.setProject(this));
        }
        this.enquiries = enquiries;
    }

    public Project enquiries(Set<Enquiry> enquiries) {
        this.setEnquiries(enquiries);
        return this;
    }

    public Project addEnquiry(Enquiry enquiry) {
        this.enquiries.add(enquiry);
        enquiry.setProject(this);
        return this;
    }

    public Project removeEnquiry(Enquiry enquiry) {
        this.enquiries.remove(enquiry);
        enquiry.setProject(null);
        return this;
    }

    public Set<ProjectActivity> getProjectActivities() {
        return this.projectActivities;
    }

    public void setProjectActivities(Set<ProjectActivity> projectActivities) {
        if (this.projectActivities != null) {
            this.projectActivities.forEach(i -> i.setProject(null));
        }
        if (projectActivities != null) {
            projectActivities.forEach(i -> i.setProject(this));
        }
        this.projectActivities = projectActivities;
    }

    public Project projectActivities(Set<ProjectActivity> projectActivities) {
        this.setProjectActivities(projectActivities);
        return this;
    }

    public Project addProjectActivity(ProjectActivity projectActivity) {
        this.projectActivities.add(projectActivity);
        projectActivity.setProject(this);
        return this;
    }

    public Project removeProjectActivity(ProjectActivity projectActivity) {
        this.projectActivities.remove(projectActivity);
        projectActivity.setProject(null);
        return this;
    }

    public ProjectType getProjectType() {
        return this.projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
        this.projectTypeId = projectType != null ? projectType.getId() : null;
    }

    public Project projectType(ProjectType projectType) {
        this.setProjectType(projectType);
        return this;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Project categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Project addCategory(Category category) {
        this.categories.add(category);
        category.getProjects().add(this);
        return this;
    }

    public Project removeCategory(Category category) {
        this.categories.remove(category);
        category.getProjects().remove(this);
        return this;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }

    public Long getProjectTypeId() {
        return this.projectTypeId;
    }

    public void setProjectTypeId(Long projectType) {
        this.projectTypeId = projectType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
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
            ", projectStatus='" + getProjectStatus() + "'" +
            ", sharableHash='" + getSharableHash() + "'" +
            ", estimatedBudget=" + getEstimatedBudget() +
            "}";
    }
}
