package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.domain.enumeration.SaleStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductVariation}, with proper type conversions.
 */
@Service
public class ProductVariationRowMapper implements BiFunction<Row, String, ProductVariation> {

    private final ColumnConverter converter;

    public ProductVariationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductVariation} stored in the database.
     */
    @Override
    public ProductVariation apply(Row row, String prefix) {
        ProductVariation entity = new ProductVariation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAssetId(converter.fromRow(row, prefix + "_asset_id", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRegularPrice(converter.fromRow(row, prefix + "_regular_price", BigDecimal.class));
        entity.setSalePrice(converter.fromRow(row, prefix + "_sale_price", BigDecimal.class));
        entity.setDateOnSaleFrom(converter.fromRow(row, prefix + "_date_on_sale_from", LocalDate.class));
        entity.setDateOnSaleTo(converter.fromRow(row, prefix + "_date_on_sale_to", LocalDate.class));
        entity.setIsDraft(converter.fromRow(row, prefix + "_is_draft", Boolean.class));
        entity.setUseParentDetails(converter.fromRow(row, prefix + "_use_parent_details", Boolean.class));
        entity.setSaleStatus(converter.fromRow(row, prefix + "_sale_status", SaleStatus.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
