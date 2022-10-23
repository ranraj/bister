package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Certification;
import com.yalisoft.bister.domain.enumeration.CertificationStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Certification}, with proper type conversions.
 */
@Service
public class CertificationRowMapper implements BiFunction<Row, String, Certification> {

    private final ColumnConverter converter;

    public CertificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Certification} stored in the database.
     */
    @Override
    public Certification apply(Row row, String prefix) {
        Certification entity = new Certification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setAuthority(converter.fromRow(row, prefix + "_authority", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CertificationStatus.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setProdcut(converter.fromRow(row, prefix + "_prodcut", Long.class));
        entity.setOrgId(converter.fromRow(row, prefix + "_org_id", Long.class));
        entity.setFacitlityId(converter.fromRow(row, prefix + "_facitlity_id", Long.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", Long.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        return entity;
    }
}
