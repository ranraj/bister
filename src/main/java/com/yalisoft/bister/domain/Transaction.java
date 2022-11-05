package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Transaction.
 */
@Table("transaction")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Column("amount")
    private BigDecimal amount;

    @NotNull(message = "must not be null")
    @Column("status")
    private TransactionStatus status;

    @Transient
    @JsonIgnoreProperties(
        value = { "paymentSchedules", "transactions", "user", "productVariation", "invoice", "customer" },
        allowSetters = true
    )
    private PurchaseOrder purchaseOrder;

    @Column("purchase_order_id")
    private Long purchaseOrderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Transaction code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transaction status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public PurchaseOrder getPurchaseOrder() {
        return this.purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderId = purchaseOrder != null ? purchaseOrder.getId() : null;
    }

    public Transaction purchaseOrder(PurchaseOrder purchaseOrder) {
        this.setPurchaseOrder(purchaseOrder);
        return this;
    }

    public Long getPurchaseOrderId() {
        return this.purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrder) {
        this.purchaseOrderId = purchaseOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
