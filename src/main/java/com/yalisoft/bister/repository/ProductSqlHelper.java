package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("slug", table, columnPrefix + "_slug"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("short_description", table, columnPrefix + "_short_description"));
        columns.add(Column.aliased("regular_price", table, columnPrefix + "_regular_price"));
        columns.add(Column.aliased("sale_price", table, columnPrefix + "_sale_price"));
        columns.add(Column.aliased("published", table, columnPrefix + "_published"));
        columns.add(Column.aliased("date_created", table, columnPrefix + "_date_created"));
        columns.add(Column.aliased("date_modified", table, columnPrefix + "_date_modified"));
        columns.add(Column.aliased("featured", table, columnPrefix + "_featured"));
        columns.add(Column.aliased("sale_status", table, columnPrefix + "_sale_status"));
        columns.add(Column.aliased("sharable_hash", table, columnPrefix + "_sharable_hash"));

        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        columns.add(Column.aliased("tax_class_id", table, columnPrefix + "_tax_class_id"));
        return columns;
    }
}
