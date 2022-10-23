package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.EnquiryResolutionStatus;
import com.yalisoft.bister.domain.enumeration.EnquiryType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Enquiry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnquiryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant raisedDate;

    @NotNull(message = "must not be null")
    private String subject;

    @Size(min = 3, max = 1000)
    private String details;

    private Instant lastResponseDate;

    private Long lastResponseId;

    @NotNull(message = "must not be null")
    private EnquiryType enquiryType;

    private EnquiryResolutionStatus status;

    private AgentDTO agent;

    private ProjectDTO project;

    private ProductDTO product;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRaisedDate() {
        return raisedDate;
    }

    public void setRaisedDate(Instant raisedDate) {
        this.raisedDate = raisedDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getLastResponseDate() {
        return lastResponseDate;
    }

    public void setLastResponseDate(Instant lastResponseDate) {
        this.lastResponseDate = lastResponseDate;
    }

    public Long getLastResponseId() {
        return lastResponseId;
    }

    public void setLastResponseId(Long lastResponseId) {
        this.lastResponseId = lastResponseId;
    }

    public EnquiryType getEnquiryType() {
        return enquiryType;
    }

    public void setEnquiryType(EnquiryType enquiryType) {
        this.enquiryType = enquiryType;
    }

    public EnquiryResolutionStatus getStatus() {
        return status;
    }

    public void setStatus(EnquiryResolutionStatus status) {
        this.status = status;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnquiryDTO)) {
            return false;
        }

        EnquiryDTO enquiryDTO = (EnquiryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enquiryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnquiryDTO{" +
            "id=" + getId() +
            ", raisedDate='" + getRaisedDate() + "'" +
            ", subject='" + getSubject() + "'" +
            ", details='" + getDetails() + "'" +
            ", lastResponseDate='" + getLastResponseDate() + "'" +
            ", lastResponseId=" + getLastResponseId() +
            ", enquiryType='" + getEnquiryType() + "'" +
            ", status='" + getStatus() + "'" +
            ", agent=" + getAgent() +
            ", project=" + getProject() +
            ", product=" + getProduct() +
            ", customer=" + getCustomer() +
            "}";
    }
}
