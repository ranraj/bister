package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TaxRate.
 */
@Table("tax_rate")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "taxrate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    @Column("country")
    private String country;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    @Column("state")
    private String state;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 20)
    @Column("postcode")
    private String postcode;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    @Column("city")
    private String city;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    @Column("rate")
    private String rate;

    @NotNull(message = "must not be null")
    @Size(min = 2, max = 250)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("compound")
    private Boolean compound;

    @NotNull(message = "must not be null")
    @Column("priority")
    private Integer priority;

    @Transient
    @JsonIgnoreProperties(value = { "taxRates", "products" }, allowSetters = true)
    private TaxClass taxClass;

    @Column("tax_class_id")
    private Long taxClassId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaxRate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return this.country;
    }

    public TaxRate country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return this.state;
    }

    public TaxRate state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public TaxRate postcode(String postcode) {
        this.setPostcode(postcode);
        return this;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return this.city;
    }

    public TaxRate city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRate() {
        return this.rate;
    }

    public TaxRate rate(String rate) {
        this.setRate(rate);
        return this;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName() {
        return this.name;
    }

    public TaxRate name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompound() {
        return this.compound;
    }

    public TaxRate compound(Boolean compound) {
        this.setCompound(compound);
        return this;
    }

    public void setCompound(Boolean compound) {
        this.compound = compound;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public TaxRate priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public TaxClass getTaxClass() {
        return this.taxClass;
    }

    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
        this.taxClassId = taxClass != null ? taxClass.getId() : null;
    }

    public TaxRate taxClass(TaxClass taxClass) {
        this.setTaxClass(taxClass);
        return this;
    }

    public Long getTaxClassId() {
        return this.taxClassId;
    }

    public void setTaxClassId(Long taxClass) {
        this.taxClassId = taxClass;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxRate)) {
            return false;
        }
        return id != null && id.equals(((TaxRate) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxRate{" +
            "id=" + getId() +
            ", country='" + getCountry() + "'" +
            ", state='" + getState() + "'" +
            ", postcode='" + getPostcode() + "'" +
            ", city='" + getCity() + "'" +
            ", rate='" + getRate() + "'" +
            ", name='" + getName() + "'" +
            ", compound='" + getCompound() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
