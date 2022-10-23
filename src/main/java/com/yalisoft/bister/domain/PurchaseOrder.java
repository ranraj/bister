package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.DeliveryOption;
import com.yalisoft.bister.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PurchaseOrder.
 */
@Table("purchase_order")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "purchaseorder")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("placed_date")
    private Instant placedDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private OrderStatus status;

    @Size(min = 20, max = 250)
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Column("delivery_option")
    private DeliveryOption deliveryOption;

    @Transient
    @JsonIgnoreProperties(value = { "invoice", "purchaseOrdep" }, allowSetters = true)
    private Set<PaymentSchedule> paymentSchedules = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "purchaseOrder" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "productVariationAttributeTerms", "product" }, allowSetters = true)
    private ProductVariation productVariation;

    @Transient
    private Invoice invoice;

    @Column("user_id")
    private Long userId;

    @Column("product_variation_id")
    private Long productVariationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchaseOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlacedDate() {
        return this.placedDate;
    }

    public PurchaseOrder placedDate(Instant placedDate) {
        this.setPlacedDate(placedDate);
        return this;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public PurchaseOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public PurchaseOrder code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DeliveryOption getDeliveryOption() {
        return this.deliveryOption;
    }

    public PurchaseOrder deliveryOption(DeliveryOption deliveryOption) {
        this.setDeliveryOption(deliveryOption);
        return this;
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public Set<PaymentSchedule> getPaymentSchedules() {
        return this.paymentSchedules;
    }

    public void setPaymentSchedules(Set<PaymentSchedule> paymentSchedules) {
        if (this.paymentSchedules != null) {
            this.paymentSchedules.forEach(i -> i.setPurchaseOrdep(null));
        }
        if (paymentSchedules != null) {
            paymentSchedules.forEach(i -> i.setPurchaseOrdep(this));
        }
        this.paymentSchedules = paymentSchedules;
    }

    public PurchaseOrder paymentSchedules(Set<PaymentSchedule> paymentSchedules) {
        this.setPaymentSchedules(paymentSchedules);
        return this;
    }

    public PurchaseOrder addPaymentSchedule(PaymentSchedule paymentSchedule) {
        this.paymentSchedules.add(paymentSchedule);
        paymentSchedule.setPurchaseOrdep(this);
        return this;
    }

    public PurchaseOrder removePaymentSchedule(PaymentSchedule paymentSchedule) {
        this.paymentSchedules.remove(paymentSchedule);
        paymentSchedule.setPurchaseOrdep(null);
        return this;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setPurchaseOrder(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setPurchaseOrder(this));
        }
        this.transactions = transactions;
    }

    public PurchaseOrder transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
        return this;
    }

    public PurchaseOrder addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setPurchaseOrder(this);
        return this;
    }

    public PurchaseOrder removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setPurchaseOrder(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public PurchaseOrder user(User user) {
        this.setUser(user);
        return this;
    }

    public ProductVariation getProductVariation() {
        return this.productVariation;
    }

    public void setProductVariation(ProductVariation productVariation) {
        this.productVariation = productVariation;
        this.productVariationId = productVariation != null ? productVariation.getId() : null;
    }

    public PurchaseOrder productVariation(ProductVariation productVariation) {
        this.setProductVariation(productVariation);
        return this;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        if (this.invoice != null) {
            this.invoice.setPurchaseOrder(null);
        }
        if (invoice != null) {
            invoice.setPurchaseOrder(this);
        }
        this.invoice = invoice;
    }

    public PurchaseOrder invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
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
        if (!(o instanceof PurchaseOrder)) {
            return false;
        }
        return id != null && id.equals(((PurchaseOrder) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseOrder{" +
            "id=" + getId() +
            ", placedDate='" + getPlacedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", code='" + getCode() + "'" +
            ", deliveryOption='" + getDeliveryOption() + "'" +
            "}";
    }
}
