package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProjectSpecificationGroup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectSpecificationGroupDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String title;

    private String slug;

    private String description;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectSpecificationGroupDTO)) {
            return false;
        }

        ProjectSpecificationGroupDTO projectSpecificationGroupDTO = (ProjectSpecificationGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectSpecificationGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSpecificationGroupDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
