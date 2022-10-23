package com.yalisoft.bister.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Organisation.
 */
@Table("organisation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "organisation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organisation implements Serializable {

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
    @Column("yali_key")
    private String key;

    @Transient
    private Address address;

    @Transient
    private BusinessPartner businessPartner;

    @Column("address_id")
    private Long addressId;

    @Column("business_partner_id")
    private Long businessPartnerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organisation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Organisation name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Organisation description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public Organisation key(String key) {
        this.setKey(key);
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Organisation address(Address address) {
        this.setAddress(address);
        return this;
    }

    public BusinessPartner getBusinessPartner() {
        return this.businessPartner;
    }

    public void setBusinessPartner(BusinessPartner businessPartner) {
        this.businessPartner = businessPartner;
        this.businessPartnerId = businessPartner != null ? businessPartner.getId() : null;
    }

    public Organisation businessPartner(BusinessPartner businessPartner) {
        this.setBusinessPartner(businessPartner);
        return this;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }

    public Long getBusinessPartnerId() {
        return this.businessPartnerId;
    }

    public void setBusinessPartnerId(Long businessPartner) {
        this.businessPartnerId = businessPartner;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organisation)) {
            return false;
        }
        return id != null && id.equals(((Organisation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organisation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", key='" + getKey() + "'" +
            "}";
    }
}
