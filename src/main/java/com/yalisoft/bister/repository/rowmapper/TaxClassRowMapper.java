package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.TaxClass;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TaxClass}, with proper type conversions.
 */
@Service
public class TaxClassRowMapper implements BiFunction<Row, String, TaxClass> {

    private final ColumnConverter converter;

    public TaxClassRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TaxClass} stored in the database.
     */
    @Override
    public TaxClass apply(Row row, String prefix) {
        TaxClass entity = new TaxClass();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        return entity;
    }
}
