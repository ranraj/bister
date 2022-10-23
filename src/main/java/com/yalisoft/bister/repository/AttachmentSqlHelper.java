package com.yalisoft.bister.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AttachmentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("attachment_type", table, columnPrefix + "_attachment_type"));
        columns.add(Column.aliased("link", table, columnPrefix + "_link"));
        columns.add(Column.aliased("is_approval_needed", table, columnPrefix + "_is_approval_needed"));
        columns.add(Column.aliased("approval_status", table, columnPrefix + "_approval_status"));
        columns.add(Column.aliased("approved_by", table, columnPrefix + "_approved_by"));
        columns.add(Column.aliased("attachment_source_type", table, columnPrefix + "_attachment_source_type"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("agent_id", table, columnPrefix + "_agent_id"));
        columns.add(Column.aliased("attachment_visibility_type", table, columnPrefix + "_attachment_visibility_type"));
        columns.add(Column.aliased("original_filename", table, columnPrefix + "_original_filename"));
        columns.add(Column.aliased("extension", table, columnPrefix + "_extension"));
        columns.add(Column.aliased("size_in_bytes", table, columnPrefix + "_size_in_bytes"));
        columns.add(Column.aliased("sha_256", table, columnPrefix + "_sha_256"));
        columns.add(Column.aliased("content_type", table, columnPrefix + "_content_type"));

        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        columns.add(Column.aliased("enquiry_id", table, columnPrefix + "_enquiry_id"));
        columns.add(Column.aliased("certification_id", table, columnPrefix + "_certification_id"));
        columns.add(Column.aliased("product_specification_id", table, columnPrefix + "_product_specification_id"));
        columns.add(Column.aliased("project_specification_id", table, columnPrefix + "_project_specification_id"));
        return columns;
    }
}
