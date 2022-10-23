package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Tag;
import com.yalisoft.bister.domain.enumeration.TagType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tag}, with proper type conversions.
 */
@Service
public class TagRowMapper implements BiFunction<Row, String, Tag> {

    private final ColumnConverter converter;

    public TagRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tag} stored in the database.
     */
    @Override
    public Tag apply(Row row, String prefix) {
        Tag entity = new Tag();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setTagType(converter.fromRow(row, prefix + "_tag_type", TagType.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setAttachmentId(converter.fromRow(row, prefix + "_attachment_id", Long.class));
        return entity;
    }
}
