package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProjectType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectType}, with proper type conversions.
 */
@Service
public class ProjectTypeRowMapper implements BiFunction<Row, String, ProjectType> {

    private final ColumnConverter converter;

    public ProjectTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectType} stored in the database.
     */
    @Override
    public ProjectType apply(Row row, String prefix) {
        ProjectType entity = new ProjectType();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
