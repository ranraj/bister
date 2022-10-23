package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductSpecificationGroup;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductSpecificationGroup}, with proper type conversions.
 */
@Service
public class ProductSpecificationGroupRowMapper implements BiFunction<Row, String, ProductSpecificationGroup> {

    private final ColumnConverter converter;

    public ProductSpecificationGroupRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductSpecificationGroup} stored in the database.
     */
    @Override
    public ProductSpecificationGroup apply(Row row, String prefix) {
        ProductSpecificationGroup entity = new ProductSpecificationGroup();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
