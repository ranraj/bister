package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.enumeration.EnquiryResolutionStatus;
import com.yalisoft.bister.domain.enumeration.EnquiryType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Enquiry}, with proper type conversions.
 */
@Service
public class EnquiryRowMapper implements BiFunction<Row, String, Enquiry> {

    private final ColumnConverter converter;

    public EnquiryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Enquiry} stored in the database.
     */
    @Override
    public Enquiry apply(Row row, String prefix) {
        Enquiry entity = new Enquiry();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRaisedDate(converter.fromRow(row, prefix + "_raised_date", Instant.class));
        entity.setSubject(converter.fromRow(row, prefix + "_subject", String.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setLastResponseDate(converter.fromRow(row, prefix + "_last_response_date", Instant.class));
        entity.setLastResponseId(converter.fromRow(row, prefix + "_last_response_id", Long.class));
        entity.setEnquiryType(converter.fromRow(row, prefix + "_enquiry_type", EnquiryType.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", EnquiryResolutionStatus.class));
        entity.setAgentId(converter.fromRow(row, prefix + "_agent_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
