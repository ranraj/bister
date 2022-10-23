package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.AgentType;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Business Agents
 */
@Table("agent")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "agent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 15)
    @Column("contact_number")
    private String contactNumber;

    @Column("avatar_url")
    private String avatarUrl;

    @NotNull(message = "must not be null")
    @Column("agent_type")
    private AgentType agentType;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "address", "user", "organisation" }, allowSetters = true)
    private Facility facility;

    @Column("user_id")
    private Long userId;

    @Column("facility_id")
    private Long facilityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Agent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Agent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public Agent contactNumber(String contactNumber) {
        this.setContactNumber(contactNumber);
        return this;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public Agent avatarUrl(String avatarUrl) {
        this.setAvatarUrl(avatarUrl);
        return this;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public AgentType getAgentType() {
        return this.agentType;
    }

    public Agent agentType(AgentType agentType) {
        this.setAgentType(agentType);
        return this;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Agent user(User user) {
        this.setUser(user);
        return this;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
        this.facilityId = facility != null ? facility.getId() : null;
    }

    public Agent facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(Long facility) {
        this.facilityId = facility;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        return id != null && id.equals(((Agent) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", avatarUrl='" + getAvatarUrl() + "'" +
            ", agentType='" + getAgentType() + "'" +
            "}";
    }
}
