package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.TaxRate;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TaxRate}, with proper type conversions.
 */
@Service
public class TaxRateRowMapper implements BiFunction<Row, String, TaxRate> {

    private final ColumnConverter converter;

    public TaxRateRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TaxRate} stored in the database.
     */
    @Override
    public TaxRate apply(Row row, String prefix) {
        TaxRate entity = new TaxRate();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setState(converter.fromRow(row, prefix + "_state", String.class));
        entity.setPostcode(converter.fromRow(row, prefix + "_postcode", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setRate(converter.fromRow(row, prefix + "_rate", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCompound(converter.fromRow(row, prefix + "_compound", Boolean.class));
        entity.setPriority(converter.fromRow(row, prefix + "_priority", Integer.class));
        entity.setTaxClassId(converter.fromRow(row, prefix + "_tax_class_id", Long.class));
        return entity;
    }
}
