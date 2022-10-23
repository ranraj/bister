package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.enumeration.AgentType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Agent}, with proper type conversions.
 */
@Service
public class AgentRowMapper implements BiFunction<Row, String, Agent> {

    private final ColumnConverter converter;

    public AgentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Agent} stored in the database.
     */
    @Override
    public Agent apply(Row row, String prefix) {
        Agent entity = new Agent();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setContactNumber(converter.fromRow(row, prefix + "_contact_number", String.class));
        entity.setAvatarUrl(converter.fromRow(row, prefix + "_avatar_url", String.class));
        entity.setAgentType(converter.fromRow(row, prefix + "_agent_type", AgentType.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setFacilityId(converter.fromRow(row, prefix + "_facility_id", Long.class));
        return entity;
    }
}
