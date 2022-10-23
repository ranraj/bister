package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectSpecificationGroup}, with proper type conversions.
 */
@Service
public class ProjectSpecificationGroupRowMapper implements BiFunction<Row, String, ProjectSpecificationGroup> {

    private final ColumnConverter converter;

    public ProjectSpecificationGroupRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectSpecificationGroup} stored in the database.
     */
    @Override
    public ProjectSpecificationGroup apply(Row row, String prefix) {
        ProjectSpecificationGroup entity = new ProjectSpecificationGroup();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
