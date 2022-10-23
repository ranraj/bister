package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductVariationAttributeTermSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("slug", table, columnPrefix + "_slug"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("menu_order", table, columnPrefix + "_menu_order"));
        columns.add(Column.aliased("over_ride_product_attribute", table, columnPrefix + "_over_ride_product_attribute"));

        columns.add(Column.aliased("product_variation_id", table, columnPrefix + "_product_variation_id"));
        return columns;
    }
}
