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
 * A ProjectActivity.
 */
@Table("project_activity")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectactivity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectActivity implements Serializable {

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

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectActivity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ProjectActivity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return this.details;
    }

    public ProjectActivity details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ActivityStatus getStatus() {
        return this.status;
    }

    public ProjectActivity status(ActivityStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public ProjectActivity project(Project project) {
        this.setProject(project);
        return this;
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
        if (!(o instanceof ProjectActivity)) {
            return false;
        }
        return id != null && id.equals(((ProjectActivity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectActivity{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
