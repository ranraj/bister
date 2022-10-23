package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductVariationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("asset_id", table, columnPrefix + "_asset_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("regular_price", table, columnPrefix + "_regular_price"));
        columns.add(Column.aliased("sale_price", table, columnPrefix + "_sale_price"));
        columns.add(Column.aliased("date_on_sale_from", table, columnPrefix + "_date_on_sale_from"));
        columns.add(Column.aliased("date_on_sale_to", table, columnPrefix + "_date_on_sale_to"));
        columns.add(Column.aliased("is_draft", table, columnPrefix + "_is_draft"));
        columns.add(Column.aliased("use_parent_details", table, columnPrefix + "_use_parent_details"));
        columns.add(Column.aliased("sale_status", table, columnPrefix + "_sale_status"));

        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        return columns;
    }
}
