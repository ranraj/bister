package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.PhonenumberType;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Phonenumber.
 */
@Table("phonenumber")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "phonenumber")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Phonenumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(min = 3, max = 50)
    @Column("country")
    private String country;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 10)
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 15)
    @Column("contact_number")
    private String contactNumber;

    @NotNull(message = "must not be null")
    @Column("phonenumber_type")
    private PhonenumberType phonenumberType;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "address", "businessPartner" }, allowSetters = true)
    private Organisation organisation;

    @Transient
    @JsonIgnoreProperties(value = { "address", "user", "organisation" }, allowSetters = true)
    private Facility facility;

    @Column("user_id")
    private Long userId;

    @Column("organisation_id")
    private Long organisationId;

    @Column("facility_id")
    private Long facilityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Phonenumber id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return this.country;
    }

    public Phonenumber country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return this.code;
    }

    public Phonenumber code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public Phonenumber contactNumber(String contactNumber) {
        this.setContactNumber(contactNumber);
        return this;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public PhonenumberType getPhonenumberType() {
        return this.phonenumberType;
    }

    public Phonenumber phonenumberType(PhonenumberType phonenumberType) {
        this.setPhonenumberType(phonenumberType);
        return this;
    }

    public void setPhonenumberType(PhonenumberType phonenumberType) {
        this.phonenumberType = phonenumberType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Phonenumber user(User user) {
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

    public Phonenumber organisation(Organisation organisation) {
        this.setOrganisation(organisation);
        return this;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
        this.facilityId = facility != null ? facility.getId() : null;
    }

    public Phonenumber facility(Facility facility) {
        this.setFacility(facility);
        return this;
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
        if (!(o instanceof Phonenumber)) {
            return false;
        }
        return id != null && id.equals(((Phonenumber) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Phonenumber{" +
            "id=" + getId() +
            ", country='" + getCountry() + "'" +
            ", code='" + getCode() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", phonenumberType='" + getPhonenumberType() + "'" +
            "}";
    }
}
