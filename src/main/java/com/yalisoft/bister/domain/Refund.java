package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.RefundStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Refund.
 */
@Table("refund")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "refund")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Refund implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("amount")
    private BigDecimal amount;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 1000)
    @Column("reason")
    private String reason;

    @NotNull(message = "must not be null")
    @Column("order_code")
    private Long orderCode;

    @NotNull(message = "must not be null")
    @Column("status")
    private RefundStatus status;

    @Transient
    private Transaction transaction;

    @Transient
    private User user;

    @Column("transaction_id")
    private Long transactionId;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Refund id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Refund amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public String getReason() {
        return this.reason;
    }

    public Refund reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOrderCode() {
        return this.orderCode;
    }

    public Refund orderCode(Long orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }

    public RefundStatus getStatus() {
        return this.status;
    }

    public Refund status(RefundStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.transactionId = transaction != null ? transaction.getId() : null;
    }

    public Refund transaction(Transaction transaction) {
        this.setTransaction(transaction);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Refund user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transaction) {
        this.transactionId = transaction;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Refund)) {
            return false;
        }
        return id != null && id.equals(((Refund) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Refund{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", reason='" + getReason() + "'" +
            ", orderCode=" + getOrderCode() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
