package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.CategoryType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Category.
 */
@Table("category")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

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

    @Size(min = 20, max = 1000)
    @Column("description")
    private String description;

    @Column("category_type")
    private CategoryType categoryType;

    @Transient
    @JsonIgnoreProperties(value = { "parent", "products", "projects" }, allowSetters = true)
    private Category parent;

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
    private Set<Project> projects = new HashSet<>();

    @Column("parent_id")
    private Long parentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Category name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Category slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryType getCategoryType() {
        return this.categoryType;
    }

    public Category categoryType(CategoryType categoryType) {
        this.setCategoryType(categoryType);
        return this;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public Category getParent() {
        return this.parent;
    }

    public void setParent(Category category) {
        this.parent = category;
        this.parentId = category != null ? category.getId() : null;
    }

    public Category parent(Category category) {
        this.setParent(category);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeCategory(this));
        }
        if (products != null) {
            products.forEach(i -> i.addCategory(this));
        }
        this.products = products;
    }

    public Category products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Category addProduct(Product product) {
        this.products.add(product);
        product.getCategories().add(this);
        return this;
    }

    public Category removeProduct(Product product) {
        this.products.remove(product);
        product.getCategories().remove(this);
        return this;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<Project> projects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.removeCategory(this));
        }
        if (projects != null) {
            projects.forEach(i -> i.addCategory(this));
        }
        this.projects = projects;
    }

    public Category projects(Set<Project> projects) {
        this.setProjects(projects);
        return this;
    }

    public Category addProject(Project project) {
        this.projects.add(project);
        project.getCategories().add(this);
        return this;
    }

    public Category removeProject(Project project) {
        this.projects.remove(project);
        project.getCategories().remove(this);
        return this;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long category) {
        this.parentId = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", categoryType='" + getCategoryType() + "'" +
            "}";
    }
}
