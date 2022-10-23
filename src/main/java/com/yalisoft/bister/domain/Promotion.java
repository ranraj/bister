package com.yalisoft.bister.domain;

import com.yalisoft.bister.domain.enumeration.PromotionContentType;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Promotion.
 */
@Table("promotion")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "promotion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("project_id")
    private Long projectId;

    @NotNull(message = "must not be null")
    @Column("content_type")
    private PromotionContentType contentType;

    @Column("recipients")
    private String recipients;

    @Column("recipient_group")
    private String recipientGroup;

    @Column("created_by")
    private Long createdBy;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @NotNull(message = "must not be null")
    @Column("send_at")
    private Instant sendAt;

    @Column("attachment_id")
    private Long attachmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Promotion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Promotion productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public Promotion projectId(Long projectId) {
        this.setProjectId(projectId);
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public PromotionContentType getContentType() {
        return this.contentType;
    }

    public Promotion contentType(PromotionContentType contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(PromotionContentType contentType) {
        this.contentType = contentType;
    }

    public String getRecipients() {
        return this.recipients;
    }

    public Promotion recipients(String recipients) {
        this.setRecipients(recipients);
        return this;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getRecipientGroup() {
        return this.recipientGroup;
    }

    public Promotion recipientGroup(String recipientGroup) {
        this.setRecipientGroup(recipientGroup);
        return this;
    }

    public void setRecipientGroup(String recipientGroup) {
        this.recipientGroup = recipientGroup;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Promotion createdBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Promotion createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSendAt() {
        return this.sendAt;
    }

    public Promotion sendAt(Instant sendAt) {
        this.setSendAt(sendAt);
        return this;
    }

    public void setSendAt(Instant sendAt) {
        this.sendAt = sendAt;
    }

    public Long getAttachmentId() {
        return this.attachmentId;
    }

    public Promotion attachmentId(Long attachmentId) {
        this.setAttachmentId(attachmentId);
        return this;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Promotion)) {
            return false;
        }
        return id != null && id.equals(((Promotion) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Promotion{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", projectId=" + getProjectId() +
            ", contentType='" + getContentType() + "'" +
            ", recipients='" + getRecipients() + "'" +
            ", recipientGroup='" + getRecipientGroup() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", sendAt='" + getSendAt() + "'" +
            ", attachmentId=" + getAttachmentId() +
            "}";
    }
}
