package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.NotificationMode;
import com.yalisoft.bister.domain.enumeration.NotificationSourceType;
import com.yalisoft.bister.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Notification.
 */
@Table("notification")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Size(min = 3, max = 1000)
    @Column("details")
    private String details;

    @NotNull(message = "must not be null")
    @Column("sent_date")
    private Instant sentDate;

    @Size(min = 3, max = 50)
    @Column("google_notification_id")
    private String googleNotificationId;

    @Size(min = 3, max = 50)
    @Column("whatsapp_notification_id")
    private String whatsappNotificationId;

    @Size(min = 3, max = 50)
    @Column("sms_notification_id")
    private String smsNotificationId;

    @Column("product_id")
    private Long productId;

    @Column("project_id")
    private Long projectId;

    @Column("schedule_id")
    private Long scheduleId;

    @Column("promotion_id")
    private Long promotionId;

    @NotNull(message = "must not be null")
    @Column("yali_read")
    private Boolean read;

    @NotNull(message = "must not be null")
    @Column("notification_source_type")
    private NotificationSourceType notificationSourceType;

    @Column("notification_type")
    private NotificationType notificationType;

    @NotNull(message = "must not be null")
    @Column("notification_mode")
    private NotificationMode notificationMode;

    @Transient
    private Agent agent;

    @Transient
    private User user;

    @Column("agent_id")
    private Long agentId;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public Notification date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDetails() {
        return this.details;
    }

    public Notification details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getSentDate() {
        return this.sentDate;
    }

    public Notification sentDate(Instant sentDate) {
        this.setSentDate(sentDate);
        return this;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }

    public String getGoogleNotificationId() {
        return this.googleNotificationId;
    }

    public Notification googleNotificationId(String googleNotificationId) {
        this.setGoogleNotificationId(googleNotificationId);
        return this;
    }

    public void setGoogleNotificationId(String googleNotificationId) {
        this.googleNotificationId = googleNotificationId;
    }

    public String getWhatsappNotificationId() {
        return this.whatsappNotificationId;
    }

    public Notification whatsappNotificationId(String whatsappNotificationId) {
        this.setWhatsappNotificationId(whatsappNotificationId);
        return this;
    }

    public void setWhatsappNotificationId(String whatsappNotificationId) {
        this.whatsappNotificationId = whatsappNotificationId;
    }

    public String getSmsNotificationId() {
        return this.smsNotificationId;
    }

    public Notification smsNotificationId(String smsNotificationId) {
        this.setSmsNotificationId(smsNotificationId);
        return this;
    }

    public void setSmsNotificationId(String smsNotificationId) {
        this.smsNotificationId = smsNotificationId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Notification productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public Notification projectId(Long projectId) {
        this.setProjectId(projectId);
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getScheduleId() {
        return this.scheduleId;
    }

    public Notification scheduleId(Long scheduleId) {
        this.setScheduleId(scheduleId);
        return this;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getPromotionId() {
        return this.promotionId;
    }

    public Notification promotionId(Long promotionId) {
        this.setPromotionId(promotionId);
        return this;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Boolean getRead() {
        return this.read;
    }

    public Notification read(Boolean read) {
        this.setRead(read);
        return this;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationSourceType getNotificationSourceType() {
        return this.notificationSourceType;
    }

    public Notification notificationSourceType(NotificationSourceType notificationSourceType) {
        this.setNotificationSourceType(notificationSourceType);
        return this;
    }

    public void setNotificationSourceType(NotificationSourceType notificationSourceType) {
        this.notificationSourceType = notificationSourceType;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public Notification notificationType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationMode getNotificationMode() {
        return this.notificationMode;
    }

    public Notification notificationMode(NotificationMode notificationMode) {
        this.setNotificationMode(notificationMode);
        return this;
    }

    public void setNotificationMode(NotificationMode notificationMode) {
        this.notificationMode = notificationMode;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
        this.agentId = agent != null ? agent.getId() : null;
    }

    public Notification agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Notification user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getAgentId() {
        return this.agentId;
    }

    public void setAgentId(Long agent) {
        this.agentId = agent;
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
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", details='" + getDetails() + "'" +
            ", sentDate='" + getSentDate() + "'" +
            ", googleNotificationId='" + getGoogleNotificationId() + "'" +
            ", whatsappNotificationId='" + getWhatsappNotificationId() + "'" +
            ", smsNotificationId='" + getSmsNotificationId() + "'" +
            ", productId=" + getProductId() +
            ", projectId=" + getProjectId() +
            ", scheduleId=" + getScheduleId() +
            ", promotionId=" + getPromotionId() +
            ", read='" + getRead() + "'" +
            ", notificationSourceType='" + getNotificationSourceType() + "'" +
            ", notificationType='" + getNotificationType() + "'" +
            ", notificationMode='" + getNotificationMode() + "'" +
            "}";
    }
}
