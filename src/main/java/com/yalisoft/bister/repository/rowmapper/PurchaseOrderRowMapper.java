package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.domain.enumeration.DeliveryOption;
import com.yalisoft.bister.domain.enumeration.OrderStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PurchaseOrder}, with proper type conversions.
 */
@Service
public class PurchaseOrderRowMapper implements BiFunction<Row, String, PurchaseOrder> {

    private final ColumnConverter converter;

    public PurchaseOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PurchaseOrder} stored in the database.
     */
    @Override
    public PurchaseOrder apply(Row row, String prefix) {
        PurchaseOrder entity = new PurchaseOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPlacedDate(converter.fromRow(row, prefix + "_placed_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OrderStatus.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDeliveryOption(converter.fromRow(row, prefix + "_delivery_option", DeliveryOption.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setProductVariationId(converter.fromRow(row, prefix + "_product_variation_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
