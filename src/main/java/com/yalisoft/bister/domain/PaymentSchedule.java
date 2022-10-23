package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.PaymentScheduleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PaymentSchedule.
 */
@Table("payment_schedule")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "paymentschedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("due_date")
    private Instant dueDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("total_price")
    private BigDecimal totalPrice;

    @Size(min = 20, max = 250)
    @Column("remarks")
    private String remarks;

    @NotNull(message = "must not be null")
    @Column("status")
    private PaymentScheduleStatus status;

    @Column("is_over_due")
    private Boolean isOverDue;

    @Transient
    private Invoice invoice;

    @Transient
    @JsonIgnoreProperties(value = { "paymentSchedules", "transactions", "user", "productVariation", "invoice" }, allowSetters = true)
    private PurchaseOrder purchaseOrdep;

    @Column("invoice_id")
    private Long invoiceId;

    @Column("purchase_ordep_id")
    private Long purchaseOrdepId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentSchedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public PaymentSchedule dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public PaymentSchedule totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public PaymentSchedule remarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public PaymentScheduleStatus getStatus() {
        return this.status;
    }

    public PaymentSchedule status(PaymentScheduleStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentScheduleStatus status) {
        this.status = status;
    }

    public Boolean getIsOverDue() {
        return this.isOverDue;
    }

    public PaymentSchedule isOverDue(Boolean isOverDue) {
        this.setIsOverDue(isOverDue);
        return this;
    }

    public void setIsOverDue(Boolean isOverDue) {
        this.isOverDue = isOverDue;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        this.invoiceId = invoice != null ? invoice.getId() : null;
    }

    public PaymentSchedule invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public PurchaseOrder getPurchaseOrdep() {
        return this.purchaseOrdep;
    }

    public void setPurchaseOrdep(PurchaseOrder purchaseOrder) {
        this.purchaseOrdep = purchaseOrder;
        this.purchaseOrdepId = purchaseOrder != null ? purchaseOrder.getId() : null;
    }

    public PaymentSchedule purchaseOrdep(PurchaseOrder purchaseOrder) {
        this.setPurchaseOrdep(purchaseOrder);
        return this;
    }

    public Long getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(Long invoice) {
        this.invoiceId = invoice;
    }

    public Long getPurchaseOrdepId() {
        return this.purchaseOrdepId;
    }

    public void setPurchaseOrdepId(Long purchaseOrder) {
        this.purchaseOrdepId = purchaseOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentSchedule)) {
            return false;
        }
        return id != null && id.equals(((PaymentSchedule) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentSchedule{" +
            "id=" + getId() +
            ", dueDate='" + getDueDate() + "'" +
            ", totalPrice=" + getTotalPrice() +
            ", remarks='" + getRemarks() + "'" +
            ", status='" + getStatus() + "'" +
            ", isOverDue='" + getIsOverDue() + "'" +
            "}";
    }
}
