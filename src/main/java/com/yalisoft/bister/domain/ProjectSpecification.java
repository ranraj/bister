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
 * A ProjectSpecification.
 */
@Table("project_specification")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectspecification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectSpecification implements Serializable {

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
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private ProjectSpecificationGroup projectSpecificationGroup;

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

    @Column("project_specification_group_id")
    private Long projectSpecificationGroupId;

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectSpecification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ProjectSpecification title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return this.value;
    }

    public ProjectSpecification value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public ProjectSpecification description(String description) {
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
            this.attachments.forEach(i -> i.setProjectSpecification(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setProjectSpecification(this));
        }
        this.attachments = attachments;
    }

    public ProjectSpecification attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public ProjectSpecification addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setProjectSpecification(this);
        return this;
    }

    public ProjectSpecification removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setProjectSpecification(null);
        return this;
    }

    public ProjectSpecificationGroup getProjectSpecificationGroup() {
        return this.projectSpecificationGroup;
    }

    public void setProjectSpecificationGroup(ProjectSpecificationGroup projectSpecificationGroup) {
        this.projectSpecificationGroup = projectSpecificationGroup;
        this.projectSpecificationGroupId = projectSpecificationGroup != null ? projectSpecificationGroup.getId() : null;
    }

    public ProjectSpecification projectSpecificationGroup(ProjectSpecificationGroup projectSpecificationGroup) {
        this.setProjectSpecificationGroup(projectSpecificationGroup);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public ProjectSpecification project(Project project) {
        this.setProject(project);
        return this;
    }

    public Long getProjectSpecificationGroupId() {
        return this.projectSpecificationGroupId;
    }

    public void setProjectSpecificationGroupId(Long projectSpecificationGroup) {
        this.projectSpecificationGroupId = projectSpecificationGroup;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectSpecification)) {
            return false;
        }
        return id != null && id.equals(((ProjectSpecification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSpecification{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
