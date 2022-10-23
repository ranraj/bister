package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.FacilityType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Facility} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FacilityDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 250)
    private String description;

    @NotNull(message = "must not be null")
    private FacilityType facilityType;

    private AddressDTO address;

    private UserDTO user;

    private OrganisationDTO organisation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityDTO)) {
            return false;
        }

        FacilityDTO facilityDTO = (FacilityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, facilityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FacilityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", facilityType='" + getFacilityType() + "'" +
            ", address=" + getAddress() +
            ", user=" + getUser() +
            ", organisation=" + getOrganisation() +
            "}";
    }
}
