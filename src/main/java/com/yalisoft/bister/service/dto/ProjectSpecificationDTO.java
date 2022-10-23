package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProjectSpecification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectSpecificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 100)
    private String value;

    @Size(min = 20, max = 250)
    private String description;

    private ProjectSpecificationGroupDTO projectSpecificationGroup;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectSpecificationGroupDTO getProjectSpecificationGroup() {
        return projectSpecificationGroup;
    }

    public void setProjectSpecificationGroup(ProjectSpecificationGroupDTO projectSpecificationGroup) {
        this.projectSpecificationGroup = projectSpecificationGroup;
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
        if (!(o instanceof ProjectSpecificationDTO)) {
            return false;
        }

        ProjectSpecificationDTO projectSpecificationDTO = (ProjectSpecificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectSpecificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSpecificationDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", projectSpecificationGroup=" + getProjectSpecificationGroup() +
            ", project=" + getProject() +
            "}";
    }
}
