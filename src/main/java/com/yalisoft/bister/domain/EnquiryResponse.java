package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.EnquiryResponseType;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EnquiryResponse.
 */
@Table("enquiry_response")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "enquiryresponse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnquiryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(min = 3, max = 1000)
    @Column("query")
    private String query;

    @Size(min = 3, max = 1000)
    @Column("details")
    private String details;

    @NotNull(message = "must not be null")
    @Column("enquiry_response_type")
    private EnquiryResponseType enquiryResponseType;

    @Transient
    @JsonIgnoreProperties(value = { "user", "facility" }, allowSetters = true)
    private Agent agent;

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "enquiryResponses", "agent", "project", "product", "customer" }, allowSetters = true)
    private Enquiry enquiry;

    @Column("agent_id")
    private Long agentId;

    @Column("enquiry_id")
    private Long enquiryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EnquiryResponse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return this.query;
    }

    public EnquiryResponse query(String query) {
        this.setQuery(query);
        return this;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDetails() {
        return this.details;
    }

    public EnquiryResponse details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public EnquiryResponseType getEnquiryResponseType() {
        return this.enquiryResponseType;
    }

    public EnquiryResponse enquiryResponseType(EnquiryResponseType enquiryResponseType) {
        this.setEnquiryResponseType(enquiryResponseType);
        return this;
    }

    public void setEnquiryResponseType(EnquiryResponseType enquiryResponseType) {
        this.enquiryResponseType = enquiryResponseType;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
        this.agentId = agent != null ? agent.getId() : null;
    }

    public EnquiryResponse agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public Enquiry getEnquiry() {
        return this.enquiry;
    }

    public void setEnquiry(Enquiry enquiry) {
        this.enquiry = enquiry;
        this.enquiryId = enquiry != null ? enquiry.getId() : null;
    }

    public EnquiryResponse enquiry(Enquiry enquiry) {
        this.setEnquiry(enquiry);
        return this;
    }

    public Long getAgentId() {
        return this.agentId;
    }

    public void setAgentId(Long agent) {
        this.agentId = agent;
    }

    public Long getEnquiryId() {
        return this.enquiryId;
    }

    public void setEnquiryId(Long enquiry) {
        this.enquiryId = enquiry;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnquiryResponse)) {
            return false;
        }
        return id != null && id.equals(((EnquiryResponse) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnquiryResponse{" +
            "id=" + getId() +
            ", query='" + getQuery() + "'" +
            ", details='" + getDetails() + "'" +
            ", enquiryResponseType='" + getEnquiryResponseType() + "'" +
            "}";
    }
}
