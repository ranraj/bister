package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductVariationAttributeTerm}, with proper type conversions.
 */
@Service
public class ProductVariationAttributeTermRowMapper implements BiFunction<Row, String, ProductVariationAttributeTerm> {

    private final ColumnConverter converter;

    public ProductVariationAttributeTermRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductVariationAttributeTerm} stored in the database.
     */
    @Override
    public ProductVariationAttributeTerm apply(Row row, String prefix) {
        ProductVariationAttributeTerm entity = new ProductVariationAttributeTerm();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setMenuOrder(converter.fromRow(row, prefix + "_menu_order", Integer.class));
        entity.setOverRideProductAttribute(converter.fromRow(row, prefix + "_over_ride_product_attribute", Boolean.class));
        entity.setProductVariationId(converter.fromRow(row, prefix + "_product_variation_id", Long.class));
        return entity;
    }
}
