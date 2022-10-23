package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.EnquiryResponse;
import com.yalisoft.bister.domain.enumeration.EnquiryResponseType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EnquiryResponse}, with proper type conversions.
 */
@Service
public class EnquiryResponseRowMapper implements BiFunction<Row, String, EnquiryResponse> {

    private final ColumnConverter converter;

    public EnquiryResponseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EnquiryResponse} stored in the database.
     */
    @Override
    public EnquiryResponse apply(Row row, String prefix) {
        EnquiryResponse entity = new EnquiryResponse();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuery(converter.fromRow(row, prefix + "_query", String.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setEnquiryResponseType(converter.fromRow(row, prefix + "_enquiry_response_type", EnquiryResponseType.class));
        entity.setAgentId(converter.fromRow(row, prefix + "_agent_id", Long.class));
        entity.setEnquiryId(converter.fromRow(row, prefix + "_enquiry_id", Long.class));
        return entity;
    }
}
