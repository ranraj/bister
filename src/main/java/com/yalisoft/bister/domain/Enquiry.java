package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.EnquiryResolutionStatus;
import com.yalisoft.bister.domain.enumeration.EnquiryType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Enquiry.
 */
@Table("enquiry")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "enquiry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Enquiry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("raised_date")
    private Instant raisedDate;

    @NotNull(message = "must not be null")
    @Column("subject")
    private String subject;

    @Size(min = 3, max = 1000)
    @Column("details")
    private String details;

    @Column("last_response_date")
    private Instant lastResponseDate;

    @Column("last_response_id")
    private Long lastResponseId;

    @NotNull(message = "must not be null")
    @Column("enquiry_type")
    private EnquiryType enquiryType;

    @Column("status")
    private EnquiryResolutionStatus status;

    @Transient
    @JsonIgnoreProperties(
        value = { "tags", "product", "project", "enquiry", "certification", "productSpecification", "projectSpecification" },
        allowSetters = true
    )
    private Set<Attachment> attachments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "agent", "enquiry" }, allowSetters = true)
    private Set<EnquiryResponse> enquiryResponses = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "user", "facility" }, allowSetters = true)
    private Agent agent;

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
    private Product product;

    @Transient
    @JsonIgnoreProperties(value = { "user", "address", "enquiries", "purchaseOrders" }, allowSetters = true)
    private Customer customer;

    @Column("agent_id")
    private Long agentId;

    @Column("project_id")
    private Long projectId;

    @Column("product_id")
    private Long productId;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enquiry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRaisedDate() {
        return this.raisedDate;
    }

    public Enquiry raisedDate(Instant raisedDate) {
        this.setRaisedDate(raisedDate);
        return this;
    }

    public void setRaisedDate(Instant raisedDate) {
        this.raisedDate = raisedDate;
    }

    public String getSubject() {
        return this.subject;
    }

    public Enquiry subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return this.details;
    }

    public Enquiry details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getLastResponseDate() {
        return this.lastResponseDate;
    }

    public Enquiry lastResponseDate(Instant lastResponseDate) {
        this.setLastResponseDate(lastResponseDate);
        return this;
    }

    public void setLastResponseDate(Instant lastResponseDate) {
        this.lastResponseDate = lastResponseDate;
    }

    public Long getLastResponseId() {
        return this.lastResponseId;
    }

    public Enquiry lastResponseId(Long lastResponseId) {
        this.setLastResponseId(lastResponseId);
        return this;
    }

    public void setLastResponseId(Long lastResponseId) {
        this.lastResponseId = lastResponseId;
    }

    public EnquiryType getEnquiryType() {
        return this.enquiryType;
    }

    public Enquiry enquiryType(EnquiryType enquiryType) {
        this.setEnquiryType(enquiryType);
        return this;
    }

    public void setEnquiryType(EnquiryType enquiryType) {
        this.enquiryType = enquiryType;
    }

    public EnquiryResolutionStatus getStatus() {
        return this.status;
    }

    public Enquiry status(EnquiryResolutionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EnquiryResolutionStatus status) {
        this.status = status;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setEnquiry(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setEnquiry(this));
        }
        this.attachments = attachments;
    }

    public Enquiry attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public Enquiry addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setEnquiry(this);
        return this;
    }

    public Enquiry removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setEnquiry(null);
        return this;
    }

    public Set<EnquiryResponse> getEnquiryResponses() {
        return this.enquiryResponses;
    }

    public void setEnquiryResponses(Set<EnquiryResponse> enquiryResponses) {
        if (this.enquiryResponses != null) {
            this.enquiryResponses.forEach(i -> i.setEnquiry(null));
        }
        if (enquiryResponses != null) {
            enquiryResponses.forEach(i -> i.setEnquiry(this));
        }
        this.enquiryResponses = enquiryResponses;
    }

    public Enquiry enquiryResponses(Set<EnquiryResponse> enquiryResponses) {
        this.setEnquiryResponses(enquiryResponses);
        return this;
    }

    public Enquiry addEnquiryResponse(EnquiryResponse enquiryResponse) {
        this.enquiryResponses.add(enquiryResponse);
        enquiryResponse.setEnquiry(this);
        return this;
    }

    public Enquiry removeEnquiryResponse(EnquiryResponse enquiryResponse) {
        this.enquiryResponses.remove(enquiryResponse);
        enquiryResponse.setEnquiry(null);
        return this;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
        this.agentId = agent != null ? agent.getId() : null;
    }

    public Enquiry agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Enquiry project(Project project) {
        this.setProject(project);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public Enquiry product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Enquiry customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getAgentId() {
        return this.agentId;
    }

    public void setAgentId(Long agent) {
        this.agentId = agent;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enquiry)) {
            return false;
        }
        return id != null && id.equals(((Enquiry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enquiry{" +
            "id=" + getId() +
            ", raisedDate='" + getRaisedDate() + "'" +
            ", subject='" + getSubject() + "'" +
            ", details='" + getDetails() + "'" +
            ", lastResponseDate='" + getLastResponseDate() + "'" +
            ", lastResponseId=" + getLastResponseId() +
            ", enquiryType='" + getEnquiryType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
