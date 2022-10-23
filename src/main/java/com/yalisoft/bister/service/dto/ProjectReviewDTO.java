package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProjectReview} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectReviewDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    private String reviewerName;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String reviewerEmail;

    @NotNull(message = "must not be null")
    @Size(min = 20, max = 1000)
    private String review;

    @NotNull(message = "must not be null")
    private Integer rating;

    @NotNull(message = "must not be null")
    private ReviewStatus status;

    @NotNull(message = "must not be null")
    private Long reviewerId;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
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
        if (!(o instanceof ProjectReviewDTO)) {
            return false;
        }

        ProjectReviewDTO projectReviewDTO = (ProjectReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectReviewDTO{" +
            "id=" + getId() +
            ", reviewerName='" + getReviewerName() + "'" +
            ", reviewerEmail='" + getReviewerEmail() + "'" +
            ", review='" + getReview() + "'" +
            ", rating=" + getRating() +
            ", status='" + getStatus() + "'" +
            ", reviewerId=" + getReviewerId() +
            ", project=" + getProject() +
            "}";
    }
}
