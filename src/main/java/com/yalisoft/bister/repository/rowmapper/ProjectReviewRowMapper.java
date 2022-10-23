package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProjectReview;
import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectReview}, with proper type conversions.
 */
@Service
public class ProjectReviewRowMapper implements BiFunction<Row, String, ProjectReview> {

    private final ColumnConverter converter;

    public ProjectReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectReview} stored in the database.
     */
    @Override
    public ProjectReview apply(Row row, String prefix) {
        ProjectReview entity = new ProjectReview();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReviewerName(converter.fromRow(row, prefix + "_reviewer_name", String.class));
        entity.setReviewerEmail(converter.fromRow(row, prefix + "_reviewer_email", String.class));
        entity.setReview(converter.fromRow(row, prefix + "_review", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ReviewStatus.class));
        entity.setReviewerId(converter.fromRow(row, prefix + "_reviewer_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
