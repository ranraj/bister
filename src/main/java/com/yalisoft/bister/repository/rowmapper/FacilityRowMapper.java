package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.enumeration.FacilityType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Facility}, with proper type conversions.
 */
@Service
public class FacilityRowMapper implements BiFunction<Row, String, Facility> {

    private final ColumnConverter converter;

    public FacilityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Facility} stored in the database.
     */
    @Override
    public Facility apply(Row row, String prefix) {
        Facility entity = new Facility();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setFacilityType(converter.fromRow(row, prefix + "_facility_type", FacilityType.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setOrganisationId(converter.fromRow(row, prefix + "_organisation_id", Long.class));
        return entity;
    }
}
