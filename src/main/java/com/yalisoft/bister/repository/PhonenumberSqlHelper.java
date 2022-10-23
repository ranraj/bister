package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PhonenumberSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("country", table, columnPrefix + "_country"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("contact_number", table, columnPrefix + "_contact_number"));
        columns.add(Column.aliased("phonenumber_type", table, columnPrefix + "_phonenumber_type"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("organisation_id", table, columnPrefix + "_organisation_id"));
        columns.add(Column.aliased("facility_id", table, columnPrefix + "_facility_id"));
        return columns;
    }
}
