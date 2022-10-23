package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Phonenumber;
import com.yalisoft.bister.domain.enumeration.PhonenumberType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Phonenumber}, with proper type conversions.
 */
@Service
public class PhonenumberRowMapper implements BiFunction<Row, String, Phonenumber> {

    private final ColumnConverter converter;

    public PhonenumberRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Phonenumber} stored in the database.
     */
    @Override
    public Phonenumber apply(Row row, String prefix) {
        Phonenumber entity = new Phonenumber();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setContactNumber(converter.fromRow(row, prefix + "_contact_number", String.class));
        entity.setPhonenumberType(converter.fromRow(row, prefix + "_phonenumber_type", PhonenumberType.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setOrganisationId(converter.fromRow(row, prefix + "_organisation_id", Long.class));
        entity.setFacilityId(converter.fromRow(row, prefix + "_facility_id", Long.class));
        return entity;
    }
}
