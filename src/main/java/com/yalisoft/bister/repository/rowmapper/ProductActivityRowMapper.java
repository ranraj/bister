package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductActivity;
import com.yalisoft.bister.domain.enumeration.ActivityStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductActivity}, with proper type conversions.
 */
@Service
public class ProductActivityRowMapper implements BiFunction<Row, String, ProductActivity> {

    private final ColumnConverter converter;

    public ProductActivityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductActivity} stored in the database.
     */
    @Override
    public ProductActivity apply(Row row, String prefix) {
        ProductActivity entity = new ProductActivity();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ActivityStatus.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
