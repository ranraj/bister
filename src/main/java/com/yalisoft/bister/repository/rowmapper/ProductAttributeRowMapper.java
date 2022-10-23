package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductAttribute;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductAttribute}, with proper type conversions.
 */
@Service
public class ProductAttributeRowMapper implements BiFunction<Row, String, ProductAttribute> {

    private final ColumnConverter converter;

    public ProductAttributeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductAttribute} stored in the database.
     */
    @Override
    public ProductAttribute apply(Row row, String prefix) {
        ProductAttribute entity = new ProductAttribute();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setNotes(converter.fromRow(row, prefix + "_notes", String.class));
        entity.setVisible(converter.fromRow(row, prefix + "_visible", Boolean.class));
        return entity;
    }
}
