package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PurchaseOrderSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("placed_date", table, columnPrefix + "_placed_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("delivery_option", table, columnPrefix + "_delivery_option"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("product_variation_id", table, columnPrefix + "_product_variation_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        return columns;
    }
}
