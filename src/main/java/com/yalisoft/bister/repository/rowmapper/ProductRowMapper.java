package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.enumeration.SaleStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
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
        entity.setFeatured(converter.fromRow(row, prefix + "_featured", Boolean.class));
        entity.setSaleStatus(converter.fromRow(row, prefix + "_sale_status", SaleStatus.class));
        entity.setSharableHash(converter.fromRow(row, prefix + "_sharable_hash", String.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setTaxClassId(converter.fromRow(row, prefix + "_tax_class_id", Long.class));
        return entity;
    }
}
