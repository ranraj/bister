package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.PaymentScheduleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.PaymentSchedule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentScheduleDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant dueDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal totalPrice;

    @Size(min = 20, max = 250)
    private String remarks;

    @NotNull(message = "must not be null")
    private PaymentScheduleStatus status;

    private Boolean isOverDue;

    private InvoiceDTO invoice;

    private PurchaseOrderDTO purchaseOrdep;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public PaymentScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentScheduleStatus status) {
        this.status = status;
    }

    public Boolean getIsOverDue() {
        return isOverDue;
    }

    public void setIsOverDue(Boolean isOverDue) {
        this.isOverDue = isOverDue;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    public PurchaseOrderDTO getPurchaseOrdep() {
        return purchaseOrdep;
    }

    public void setPurchaseOrdep(PurchaseOrderDTO purchaseOrdep) {
        this.purchaseOrdep = purchaseOrdep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentScheduleDTO)) {
            return false;
        }

        PaymentScheduleDTO paymentScheduleDTO = (PaymentScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentScheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentScheduleDTO{" +
            "id=" + getId() +
            ", dueDate='" + getDueDate() + "'" +
            ", totalPrice=" + getTotalPrice() +
            ", remarks='" + getRemarks() + "'" +
            ", status='" + getStatus() + "'" +
            ", isOverDue='" + getIsOverDue() + "'" +
            ", invoice=" + getInvoice() +
            ", purchaseOrdep=" + getPurchaseOrdep() +
            "}";
    }
}
