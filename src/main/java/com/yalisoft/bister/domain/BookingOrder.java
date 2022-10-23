package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.BookingOrderStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A BookingOrder.
 */
@Table("booking_order")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bookingorder")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("placed_date")
    private Instant placedDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private BookingOrderStatus status;

    @Size(min = 20, max = 250)
    @Column("code")
    private String code;

    @Column("booking_expiery_date")
    private Instant bookingExpieryDate;

    @Transient
    private Customer customer;

    @Transient
    @JsonIgnoreProperties(value = { "productVariationAttributeTerms", "product" }, allowSetters = true)
    private ProductVariation productVariation;

    @Column("customer_id")
    private Long customerId;

    @Column("product_variation_id")
    private Long productVariationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookingOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlacedDate() {
        return this.placedDate;
    }

    public BookingOrder placedDate(Instant placedDate) {
        this.setPlacedDate(placedDate);
        return this;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public BookingOrderStatus getStatus() {
        return this.status;
    }

    public BookingOrder status(BookingOrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookingOrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public BookingOrder code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getBookingExpieryDate() {
        return this.bookingExpieryDate;
    }

    public BookingOrder bookingExpieryDate(Instant bookingExpieryDate) {
        this.setBookingExpieryDate(bookingExpieryDate);
        return this;
    }

    public void setBookingExpieryDate(Instant bookingExpieryDate) {
        this.bookingExpieryDate = bookingExpieryDate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public BookingOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public ProductVariation getProductVariation() {
        return this.productVariation;
    }

    public void setProductVariation(ProductVariation productVariation) {
        this.productVariation = productVariation;
        this.productVariationId = productVariation != null ? productVariation.getId() : null;
    }

    public BookingOrder productVariation(ProductVariation productVariation) {
        this.setProductVariation(productVariation);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    public Long getProductVariationId() {
        return this.productVariationId;
    }

    public void setProductVariationId(Long productVariation) {
        this.productVariationId = productVariation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingOrder)) {
            return false;
        }
        return id != null && id.equals(((BookingOrder) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingOrder{" +
            "id=" + getId() +
            ", placedDate='" + getPlacedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", code='" + getCode() + "'" +
            ", bookingExpieryDate='" + getBookingExpieryDate() + "'" +
            "}";
    }
}
