package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductSpecification;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductSpecification}, with proper type conversions.
 */
@Service
public class ProductSpecificationRowMapper implements BiFunction<Row, String, ProductSpecification> {

    private final ColumnConverter converter;

    public ProductSpecificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductSpecification} stored in the database.
     */
    @Override
    public ProductSpecification apply(Row row, String prefix) {
        ProductSpecification entity = new ProductSpecification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setValue(converter.fromRow(row, prefix + "_value", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setProductSpecificationGroupId(converter.fromRow(row, prefix + "_product_specification_group_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
