package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.FacilityType;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Facility.
 */
@Table("facility")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "facility")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Facility implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 250)
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("facility_type")
    private FacilityType facilityType;

    @Transient
    private Address address;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "address", "businessPartner" }, allowSetters = true)
    private Organisation organisation;

    @Column("address_id")
    private Long addressId;

    @Column("user_id")
    private Long userId;

    @Column("organisation_id")
    private Long organisationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Facility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Facility name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Facility description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FacilityType getFacilityType() {
        return this.facilityType;
    }

    public Facility facilityType(FacilityType facilityType) {
        this.setFacilityType(facilityType);
        return this;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Facility address(Address address) {
        this.setAddress(address);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Facility user(User user) {
        this.setUser(user);
        return this;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        this.organisationId = organisation != null ? organisation.getId() : null;
    }

    public Facility organisation(Organisation organisation) {
        this.setOrganisation(organisation);
        return this;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getOrganisationId() {
        return this.organisationId;
    }

    public void setOrganisationId(Long organisation) {
        this.organisationId = organisation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Facility)) {
            return false;
        }
        return id != null && id.equals(((Facility) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Facility{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", facilityType='" + getFacilityType() + "'" +
            "}";
    }
}
