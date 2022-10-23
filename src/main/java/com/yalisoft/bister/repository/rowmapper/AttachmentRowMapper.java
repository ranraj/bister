package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.domain.enumeration.AttachmentApprovalStatus;
import com.yalisoft.bister.domain.enumeration.AttachmentSourceType;
import com.yalisoft.bister.domain.enumeration.AttachmentType;
import com.yalisoft.bister.domain.enumeration.AttachmentVisibilityType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Attachment}, with proper type conversions.
 */
@Service
public class AttachmentRowMapper implements BiFunction<Row, String, Attachment> {

    private final ColumnConverter converter;

    public AttachmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Attachment} stored in the database.
     */
    @Override
    public Attachment apply(Row row, String prefix) {
        Attachment entity = new Attachment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setAttachmentType(converter.fromRow(row, prefix + "_attachment_type", AttachmentType.class));
        entity.setLink(converter.fromRow(row, prefix + "_link", String.class));
        entity.setIsApprovalNeeded(converter.fromRow(row, prefix + "_is_approval_needed", Boolean.class));
        entity.setApprovalStatus(converter.fromRow(row, prefix + "_approval_status", AttachmentApprovalStatus.class));
        entity.setApprovedBy(converter.fromRow(row, prefix + "_approved_by", Long.class));
        entity.setAttachmentSourceType(converter.fromRow(row, prefix + "_attachment_source_type", AttachmentSourceType.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setAgentId(converter.fromRow(row, prefix + "_agent_id", Long.class));
        entity.setAttachmentVisibilityType(converter.fromRow(row, prefix + "_attachment_visibility_type", AttachmentVisibilityType.class));
        entity.setOriginalFilename(converter.fromRow(row, prefix + "_original_filename", String.class));
        entity.setExtension(converter.fromRow(row, prefix + "_extension", String.class));
        entity.setSizeInBytes(converter.fromRow(row, prefix + "_size_in_bytes", Integer.class));
        entity.setSha256(converter.fromRow(row, prefix + "_sha_256", String.class));
        entity.setContentType(converter.fromRow(row, prefix + "_content_type", String.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        entity.setEnquiryId(converter.fromRow(row, prefix + "_enquiry_id", Long.class));
        entity.setCertificationId(converter.fromRow(row, prefix + "_certification_id", Long.class));
        entity.setProductSpecificationId(converter.fromRow(row, prefix + "_product_specification_id", Long.class));
        entity.setProjectSpecificationId(converter.fromRow(row, prefix + "_project_specification_id", Long.class));
        return entity;
    }
}
