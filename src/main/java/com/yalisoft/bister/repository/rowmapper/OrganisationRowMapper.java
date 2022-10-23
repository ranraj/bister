package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Organisation;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Organisation}, with proper type conversions.
 */
@Service
public class OrganisationRowMapper implements BiFunction<Row, String, Organisation> {

    private final ColumnConverter converter;

    public OrganisationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Organisation} stored in the database.
     */
    @Override
    public Organisation apply(Row row, String prefix) {
        Organisation entity = new Organisation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setKey(converter.fromRow(row, prefix + "_yali_key", String.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        entity.setBusinessPartnerId(converter.fromRow(row, prefix + "_business_partner_id", Long.class));
        return entity;
    }
}
