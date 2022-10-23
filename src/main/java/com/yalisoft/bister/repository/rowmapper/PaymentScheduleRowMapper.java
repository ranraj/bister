package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.domain.enumeration.PaymentScheduleStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PaymentSchedule}, with proper type conversions.
 */
@Service
public class PaymentScheduleRowMapper implements BiFunction<Row, String, PaymentSchedule> {

    private final ColumnConverter converter;

    public PaymentScheduleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PaymentSchedule} stored in the database.
     */
    @Override
    public PaymentSchedule apply(Row row, String prefix) {
        PaymentSchedule entity = new PaymentSchedule();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDueDate(converter.fromRow(row, prefix + "_due_date", Instant.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", PaymentScheduleStatus.class));
        entity.setIsOverDue(converter.fromRow(row, prefix + "_is_over_due", Boolean.class));
        entity.setInvoiceId(converter.fromRow(row, prefix + "_invoice_id", Long.class));
        entity.setPurchaseOrdepId(converter.fromRow(row, prefix + "_purchase_ordep_id", Long.class));
        return entity;
    }
}
