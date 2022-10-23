package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProjectActivity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectActivityDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 100)
    private String title;

    @Size(min = 20, max = 250)
    private String details;

    @NotNull(message = "must not be null")
    private ActivityStatus status;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
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
        if (!(o instanceof ProjectActivityDTO)) {
            return false;
        }

        ProjectActivityDTO projectActivityDTO = (ProjectActivityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectActivityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectActivityDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
