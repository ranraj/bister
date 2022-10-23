package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.EnquiryResponseType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.EnquiryResponse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnquiryResponseDTO implements Serializable {

    private Long id;

    @Size(min = 3, max = 1000)
    private String query;

    @Size(min = 3, max = 1000)
    private String details;

    @NotNull(message = "must not be null")
    private EnquiryResponseType enquiryResponseType;

    private AgentDTO agent;

    private EnquiryDTO enquiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public EnquiryResponseType getEnquiryResponseType() {
        return enquiryResponseType;
    }

    public void setEnquiryResponseType(EnquiryResponseType enquiryResponseType) {
        this.enquiryResponseType = enquiryResponseType;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public EnquiryDTO getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(EnquiryDTO enquiry) {
        this.enquiry = enquiry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnquiryResponseDTO)) {
            return false;
        }

        EnquiryResponseDTO enquiryResponseDTO = (EnquiryResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enquiryResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnquiryResponseDTO{" +
            "id=" + getId() +
            ", query='" + getQuery() + "'" +
            ", details='" + getDetails() + "'" +
            ", enquiryResponseType='" + getEnquiryResponseType() + "'" +
            ", agent=" + getAgent() +
            ", enquiry=" + getEnquiry() +
            "}";
    }
}
