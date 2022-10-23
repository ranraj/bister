package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.BusinessPartner} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessPartnerDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 250)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 250)
    private String description;

    @NotNull(message = "must not be null")
    private String key;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessPartnerDTO)) {
            return false;
        }

        BusinessPartnerDTO businessPartnerDTO = (BusinessPartnerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, businessPartnerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessPartnerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", key='" + getKey() + "'" +
            "}";
    }
}
