package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.BookingOrderStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.BookingOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingOrderDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant placedDate;

    @NotNull(message = "must not be null")
    private BookingOrderStatus status;

    @Size(min = 20, max = 250)
    private String code;

    private Instant bookingExpieryDate;

    private CustomerDTO customer;

    private ProductVariationDTO productVariation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public BookingOrderStatus getStatus() {
        return status;
    }

    public void setStatus(BookingOrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getBookingExpieryDate() {
        return bookingExpieryDate;
    }

    public void setBookingExpieryDate(Instant bookingExpieryDate) {
        this.bookingExpieryDate = bookingExpieryDate;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public ProductVariationDTO getProductVariation() {
        return productVariation;
    }

    public void setProductVariation(ProductVariationDTO productVariation) {
        this.productVariation = productVariation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingOrderDTO)) {
            return false;
        }

        BookingOrderDTO bookingOrderDTO = (BookingOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookingOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingOrderDTO{" +
            "id=" + getId() +
            ", placedDate='" + getPlacedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", code='" + getCode() + "'" +
            ", bookingExpieryDate='" + getBookingExpieryDate() + "'" +
            ", customer=" + getCustomer() +
            ", productVariation=" + getProductVariation() +
            "}";
    }
}
