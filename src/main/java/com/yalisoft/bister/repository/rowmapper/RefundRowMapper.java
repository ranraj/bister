package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Refund;
import com.yalisoft.bister.domain.enumeration.RefundStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Refund}, with proper type conversions.
 */
@Service
public class RefundRowMapper implements BiFunction<Row, String, Refund> {

    private final ColumnConverter converter;

    public RefundRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Refund} stored in the database.
     */
    @Override
    public Refund apply(Row row, String prefix) {
        Refund entity = new Refund();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", BigDecimal.class));
        entity.setReason(converter.fromRow(row, prefix + "_reason", String.class));
        entity.setOrderCode(converter.fromRow(row, prefix + "_order_code", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", RefundStatus.class));
        entity.setTransactionId(converter.fromRow(row, prefix + "_transaction_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
