package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductReview;
import com.yalisoft.bister.domain.enumeration.ReviewStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductReview}, with proper type conversions.
 */
@Service
public class ProductReviewRowMapper implements BiFunction<Row, String, ProductReview> {

    private final ColumnConverter converter;

    public ProductReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductReview} stored in the database.
     */
    @Override
    public ProductReview apply(Row row, String prefix) {
        ProductReview entity = new ProductReview();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReviewerName(converter.fromRow(row, prefix + "_reviewer_name", String.class));
        entity.setReviewerEmail(converter.fromRow(row, prefix + "_reviewer_email", String.class));
        entity.setReview(converter.fromRow(row, prefix + "_review", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ReviewStatus.class));
        entity.setReviewerId(converter.fromRow(row, prefix + "_reviewer_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
