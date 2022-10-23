package com.yalisoft.bister.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.TaxRate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxRateDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    private String country;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    private String state;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 20)
    private String postcode;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    private String city;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    private String rate;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 250)
    private String name;

    @NotNull(message = "must not be null")
    private Boolean compound;

    @NotNull(message = "must not be null")
    private Integer priority;

    private TaxClassDTO taxClass;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompound() {
        return compound;
    }

    public void setCompound(Boolean compound) {
        this.compound = compound;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public TaxClassDTO getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(TaxClassDTO taxClass) {
        this.taxClass = taxClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxRateDTO)) {
            return false;
        }

        TaxRateDTO taxRateDTO = (TaxRateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taxRateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxRateDTO{" +
            "id=" + getId() +
            ", country='" + getCountry() + "'" +
            ", state='" + getState() + "'" +
            ", postcode='" + getPostcode() + "'" +
            ", city='" + getCity() + "'" +
            ", rate='" + getRate() + "'" +
            ", name='" + getName() + "'" +
            ", compound='" + getCompound() + "'" +
            ", priority=" + getPriority() +
            ", taxClass=" + getTaxClass() +
            "}";
    }
}
