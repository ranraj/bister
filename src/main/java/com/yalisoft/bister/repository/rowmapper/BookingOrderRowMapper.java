package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.BookingOrder;
import com.yalisoft.bister.domain.enumeration.BookingOrderStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BookingOrder}, with proper type conversions.
 */
@Service
public class BookingOrderRowMapper implements BiFunction<Row, String, BookingOrder> {

    private final ColumnConverter converter;

    public BookingOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BookingOrder} stored in the database.
     */
    @Override
    public BookingOrder apply(Row row, String prefix) {
        BookingOrder entity = new BookingOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPlacedDate(converter.fromRow(row, prefix + "_placed_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", BookingOrderStatus.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setBookingExpieryDate(converter.fromRow(row, prefix + "_booking_expiery_date", Instant.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setProductVariationId(converter.fromRow(row, prefix + "_product_variation_id", Long.class));
        return entity;
    }
}
