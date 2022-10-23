package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProjectActivity;
import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectActivity}, with proper type conversions.
 */
@Service
public class ProjectActivityRowMapper implements BiFunction<Row, String, ProjectActivity> {

    private final ColumnConverter converter;

    public ProjectActivityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectActivity} stored in the database.
     */
    @Override
    public ProjectActivity apply(Row row, String prefix) {
        ProjectActivity entity = new ProjectActivity();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ActivityStatus.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
