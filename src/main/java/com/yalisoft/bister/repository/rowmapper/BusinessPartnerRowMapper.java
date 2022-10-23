package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.BusinessPartner;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BusinessPartner}, with proper type conversions.
 */
@Service
public class BusinessPartnerRowMapper implements BiFunction<Row, String, BusinessPartner> {

    private final ColumnConverter converter;

    public BusinessPartnerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BusinessPartner} stored in the database.
     */
    @Override
    public BusinessPartner apply(Row row, String prefix) {
        BusinessPartner entity = new BusinessPartner();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setKey(converter.fromRow(row, prefix + "_yali_key", String.class));
        return entity;
    }
}
