package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Notification;
import com.yalisoft.bister.domain.enumeration.NotificationMode;
import com.yalisoft.bister.domain.enumeration.NotificationSourceType;
import com.yalisoft.bister.domain.enumeration.NotificationType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notification}, with proper type conversions.
 */
@Service
public class NotificationRowMapper implements BiFunction<Row, String, Notification> {

    private final ColumnConverter converter;

    public NotificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notification} stored in the database.
     */
    @Override
    public Notification apply(Row row, String prefix) {
        Notification entity = new Notification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setSentDate(converter.fromRow(row, prefix + "_sent_date", Instant.class));
        entity.setGoogleNotificationId(converter.fromRow(row, prefix + "_google_notification_id", String.class));
        entity.setWhatsappNotificationId(converter.fromRow(row, prefix + "_whatsapp_notification_id", String.class));
        entity.setSmsNotificationId(converter.fromRow(row, prefix + "_sms_notification_id", String.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setScheduleId(converter.fromRow(row, prefix + "_schedule_id", Long.class));
        entity.setPromotionId(converter.fromRow(row, prefix + "_promotion_id", Long.class));
        entity.setRead(converter.fromRow(row, prefix + "_yali_read", Boolean.class));
        entity.setNotificationSourceType(converter.fromRow(row, prefix + "_notification_source_type", NotificationSourceType.class));
        entity.setNotificationType(converter.fromRow(row, prefix + "_notification_type", NotificationType.class));
        entity.setNotificationMode(converter.fromRow(row, prefix + "_notification_mode", NotificationMode.class));
        entity.setAgentId(converter.fromRow(row, prefix + "_agent_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
