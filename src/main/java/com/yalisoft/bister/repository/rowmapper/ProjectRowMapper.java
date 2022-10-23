package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.enumeration.ProjectStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Project}, with proper type conversions.
 */
@Service
public class ProjectRowMapper implements BiFunction<Row, String, Project> {

    private final ColumnConverter converter;

    public ProjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Project} stored in the database.
     */
    @Override
    public Project apply(Row row, String prefix) {
        Project entity = new Project();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setShortDescription(converter.fromRow(row, prefix + "_short_description", String.class));
        entity.setRegularPrice(converter.fromRow(row, prefix + "_regular_price", BigDecimal.class));
        entity.setSalePrice(converter.fromRow(row, prefix + "_sale_price", BigDecimal.class));
        entity.setPublished(converter.fromRow(row, prefix + "_published", Boolean.class));
        entity.setDateCreated(converter.fromRow(row, prefix + "_date_created", Instant.class));
        entity.setDateModified(converter.fromRow(row, prefix + "_date_modified", LocalDate.class));
        entity.setProjectStatus(converter.fromRow(row, prefix + "_project_status", ProjectStatus.class));
        entity.setSharableHash(converter.fromRow(row, prefix + "_sharable_hash", String.class));
        entity.setEstimatedBudget(converter.fromRow(row, prefix + "_estimated_budget", BigDecimal.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        entity.setProjectTypeId(converter.fromRow(row, prefix + "_project_type_id", Long.class));
        return entity;
    }
}
