package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class NotificationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("details", table, columnPrefix + "_details"));
        columns.add(Column.aliased("sent_date", table, columnPrefix + "_sent_date"));
        columns.add(Column.aliased("google_notification_id", table, columnPrefix + "_google_notification_id"));
        columns.add(Column.aliased("whatsapp_notification_id", table, columnPrefix + "_whatsapp_notification_id"));
        columns.add(Column.aliased("sms_notification_id", table, columnPrefix + "_sms_notification_id"));
        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        columns.add(Column.aliased("schedule_id", table, columnPrefix + "_schedule_id"));
        columns.add(Column.aliased("promotion_id", table, columnPrefix + "_promotion_id"));
        columns.add(Column.aliased("yali_read", table, columnPrefix + "_yali_read"));
        columns.add(Column.aliased("notification_source_type", table, columnPrefix + "_notification_source_type"));
        columns.add(Column.aliased("notification_type", table, columnPrefix + "_notification_type"));
        columns.add(Column.aliased("notification_mode", table, columnPrefix + "_notification_mode"));

        columns.add(Column.aliased("agent_id", table, columnPrefix + "_agent_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
