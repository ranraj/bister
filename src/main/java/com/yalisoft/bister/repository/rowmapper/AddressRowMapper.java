package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.enumeration.AddressType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Address}, with proper type conversions.
 */
@Service
public class AddressRowMapper implements BiFunction<Row, String, Address> {

    private final ColumnConverter converter;

    public AddressRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Address} stored in the database.
     */
    @Override
    public Address apply(Row row, String prefix) {
        Address entity = new Address();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setLandmark(converter.fromRow(row, prefix + "_landmark", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setState(converter.fromRow(row, prefix + "_state", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setPostcode(converter.fromRow(row, prefix + "_postcode", String.class));
        entity.setLatitude(converter.fromRow(row, prefix + "_latitude", String.class));
        entity.setLongitude(converter.fromRow(row, prefix + "_longitude", String.class));
        entity.setAddressType(converter.fromRow(row, prefix + "_address_type", AddressType.class));
        return entity;
    }
}
