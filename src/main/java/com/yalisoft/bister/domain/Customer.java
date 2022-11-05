package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Customer.
 */
@Table("customer")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

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

    @Transient
    private User user;

    @Transient
    private Address address;

    @Transient
    @JsonIgnoreProperties(value = { "attachments", "enquiryResponses", "agent", "project", "product", "customer" }, allowSetters = true)
    private Set<Enquiry> enquiries = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = { "paymentSchedules", "transactions", "user", "productVariation", "invoice", "customer" },
        allowSetters = true
    )
    private Set<PurchaseOrder> purchaseOrders = new HashSet<>();

    @Column("user_id")
    private Long userId;

    @Column("address_id")
    private Long addressId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Customer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public Customer contactNumber(String contactNumber) {
        this.setContactNumber(contactNumber);
        return this;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public Customer avatarUrl(String avatarUrl) {
        this.setAvatarUrl(avatarUrl);
        return this;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Customer user(User user) {
        this.setUser(user);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Customer address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Set<Enquiry> getEnquiries() {
        return this.enquiries;
    }

    public void setEnquiries(Set<Enquiry> enquiries) {
        if (this.enquiries != null) {
            this.enquiries.forEach(i -> i.setCustomer(null));
        }
        if (enquiries != null) {
            enquiries.forEach(i -> i.setCustomer(this));
        }
        this.enquiries = enquiries;
    }

    public Customer enquiries(Set<Enquiry> enquiries) {
        this.setEnquiries(enquiries);
        return this;
    }

    public Customer addEnquiry(Enquiry enquiry) {
        this.enquiries.add(enquiry);
        enquiry.setCustomer(this);
        return this;
    }

    public Customer removeEnquiry(Enquiry enquiry) {
        this.enquiries.remove(enquiry);
        enquiry.setCustomer(null);
        return this;
    }

    public Set<PurchaseOrder> getPurchaseOrders() {
        return this.purchaseOrders;
    }

    public void setPurchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        if (this.purchaseOrders != null) {
            this.purchaseOrders.forEach(i -> i.setCustomer(null));
        }
        if (purchaseOrders != null) {
            purchaseOrders.forEach(i -> i.setCustomer(this));
        }
        this.purchaseOrders = purchaseOrders;
    }

    public Customer purchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        this.setPurchaseOrders(purchaseOrders);
        return this;
    }

    public Customer addPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrders.add(purchaseOrder);
        purchaseOrder.setCustomer(this);
        return this;
    }

    public Customer removePurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrders.remove(purchaseOrder);
        purchaseOrder.setCustomer(null);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", avatarUrl='" + getAvatarUrl() + "'" +
            "}";
    }
}
