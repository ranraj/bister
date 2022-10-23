package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AgentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("contact_number", table, columnPrefix + "_contact_number"));
        columns.add(Column.aliased("avatar_url", table, columnPrefix + "_avatar_url"));
        columns.add(Column.aliased("agent_type", table, columnPrefix + "_agent_type"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("facility_id", table, columnPrefix + "_facility_id"));
        return columns;
    }
}
