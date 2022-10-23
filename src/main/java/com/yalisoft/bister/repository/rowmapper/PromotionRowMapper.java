package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Promotion;
import com.yalisoft.bister.domain.enumeration.PromotionContentType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Promotion}, with proper type conversions.
 */
@Service
public class PromotionRowMapper implements BiFunction<Row, String, Promotion> {

    private final ColumnConverter converter;

    public PromotionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Promotion} stored in the database.
     */
    @Override
    public Promotion apply(Row row, String prefix) {
        Promotion entity = new Promotion();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setContentType(converter.fromRow(row, prefix + "_content_type", PromotionContentType.class));
        entity.setRecipients(converter.fromRow(row, prefix + "_recipients", String.class));
        entity.setRecipientGroup(converter.fromRow(row, prefix + "_recipient_group", String.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", Long.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setSendAt(converter.fromRow(row, prefix + "_send_at", Instant.class));
        entity.setAttachmentId(converter.fromRow(row, prefix + "_attachment_id", Long.class));
        return entity;
    }
}
