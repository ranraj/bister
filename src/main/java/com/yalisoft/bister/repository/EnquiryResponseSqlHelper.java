package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EnquiryResponseSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("query", table, columnPrefix + "_query"));
        columns.add(Column.aliased("details", table, columnPrefix + "_details"));
        columns.add(Column.aliased("enquiry_response_type", table, columnPrefix + "_enquiry_response_type"));

        columns.add(Column.aliased("agent_id", table, columnPrefix + "_agent_id"));
        columns.add(Column.aliased("enquiry_id", table, columnPrefix + "_enquiry_id"));
        return columns;
    }
}
