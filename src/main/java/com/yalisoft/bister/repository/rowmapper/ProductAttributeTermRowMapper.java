package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductAttributeTerm;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductAttributeTerm}, with proper type conversions.
 */
@Service
public class ProductAttributeTermRowMapper implements BiFunction<Row, String, ProductAttributeTerm> {

    private final ColumnConverter converter;

    public ProductAttributeTermRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductAttributeTerm} stored in the database.
     */
    @Override
    public ProductAttributeTerm apply(Row row, String prefix) {
        ProductAttributeTerm entity = new ProductAttributeTerm();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setMenuOrder(converter.fromRow(row, prefix + "_menu_order", Integer.class));
        entity.setProductAttributeId(converter.fromRow(row, prefix + "_product_attribute_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
