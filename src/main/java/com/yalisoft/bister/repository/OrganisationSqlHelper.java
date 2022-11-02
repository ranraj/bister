package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrganisationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("yali_key", table, columnPrefix + "_yali_key"));

        columns.add(Column.aliased("address_id", table, columnPrefix + "_address_id"));
        columns.add(Column.aliased("business_partner_id", table, columnPrefix + "_business_partner_id"));
        return columns;
    }
}