package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PromotionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        columns.add(Column.aliased("content_type", table, columnPrefix + "_content_type"));
        columns.add(Column.aliased("recipients", table, columnPrefix + "_recipients"));
        columns.add(Column.aliased("recipient_group", table, columnPrefix + "_recipient_group"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("send_at", table, columnPrefix + "_send_at"));
        columns.add(Column.aliased("attachment_id", table, columnPrefix + "_attachment_id"));

        return columns;
    }
}
