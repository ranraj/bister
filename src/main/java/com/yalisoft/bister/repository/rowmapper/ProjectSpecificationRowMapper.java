package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProjectSpecification;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectSpecification}, with proper type conversions.
 */
@Service
public class ProjectSpecificationRowMapper implements BiFunction<Row, String, ProjectSpecification> {

    private final ColumnConverter converter;

    public ProjectSpecificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectSpecification} stored in the database.
     */
    @Override
    public ProjectSpecification apply(Row row, String prefix) {
        ProjectSpecification entity = new ProjectSpecification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setValue(converter.fromRow(row, prefix + "_value", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setProjectSpecificationGroupId(converter.fromRow(row, prefix + "_project_specification_group_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
