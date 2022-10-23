package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.PhonenumberType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Phonenumber} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PhonenumberDTO implements Serializable {

    private Long id;

    @Size(min = 3, max = 50)
    private String country;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 10)
    private String code;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 15)
    private String contactNumber;

    @NotNull(message = "must not be null")
    private PhonenumberType phonenumberType;

    private UserDTO user;

    private OrganisationDTO organisation;

    private FacilityDTO facility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public PhonenumberType getPhonenumberType() {
        return phonenumberType;
    }

    public void setPhonenumberType(PhonenumberType phonenumberType) {
        this.phonenumberType = phonenumberType;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationDTO organisation) {
        this.organisation = organisation;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhonenumberDTO)) {
            return false;
        }

        PhonenumberDTO phonenumberDTO = (PhonenumberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, phonenumberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PhonenumberDTO{" +
            "id=" + getId() +
            ", country='" + getCountry() + "'" +
            ", code='" + getCode() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", phonenumberType='" + getPhonenumberType() + "'" +
            ", user=" + getUser() +
            ", organisation=" + getOrganisation() +
            ", facility=" + getFacility() +
            "}";
    }
}
