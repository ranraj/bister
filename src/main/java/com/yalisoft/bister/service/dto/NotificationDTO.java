package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.NotificationMode;
import com.yalisoft.bister.domain.enumeration.NotificationSourceType;
import com.yalisoft.bister.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant date;

    @Size(min = 3, max = 1000)
    private String details;

    @NotNull(message = "must not be null")
    private Instant sentDate;

    @Size(min = 3, max = 50)
    private String googleNotificationId;

    @Size(min = 3, max = 50)
    private String whatsappNotificationId;

    @Size(min = 3, max = 50)
    private String smsNotificationId;

    private Long productId;

    private Long projectId;

    private Long scheduleId;

    private Long promotionId;

    @NotNull(message = "must not be null")
    private Boolean read;

    @NotNull(message = "must not be null")
    private NotificationSourceType notificationSourceType;

    private NotificationType notificationType;

    @NotNull(message = "must not be null")
    private NotificationMode notificationMode;

    private AgentDTO agent;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }

    public String getGoogleNotificationId() {
        return googleNotificationId;
    }

    public void setGoogleNotificationId(String googleNotificationId) {
        this.googleNotificationId = googleNotificationId;
    }

    public String getWhatsappNotificationId() {
        return whatsappNotificationId;
    }

    public void setWhatsappNotificationId(String whatsappNotificationId) {
        this.whatsappNotificationId = whatsappNotificationId;
    }

    public String getSmsNotificationId() {
        return smsNotificationId;
    }

    public void setSmsNotificationId(String smsNotificationId) {
        this.smsNotificationId = smsNotificationId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationSourceType getNotificationSourceType() {
        return notificationSourceType;
    }

    public void setNotificationSourceType(NotificationSourceType notificationSourceType) {
        this.notificationSourceType = notificationSourceType;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationMode getNotificationMode() {
        return notificationMode;
    }

    public void setNotificationMode(NotificationMode notificationMode) {
        this.notificationMode = notificationMode;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
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
            ", agent=" + getAgent() +
            ", user=" + getUser() +
            "}";
    }
}
