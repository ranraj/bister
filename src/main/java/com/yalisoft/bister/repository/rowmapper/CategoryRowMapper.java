package com.yalisoft.bister.repository.rowmapper;

import com.yalisoft.bister.domain.Category;
import com.yalisoft.bister.domain.enumeration.CategoryType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Category}, with proper type conversions.
 */
@Service
public class CategoryRowMapper implements BiFunction<Row, String, Category> {

    private final ColumnConverter converter;

    public CategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Category} stored in the database.
     */
    @Override
    public Category apply(Row row, String prefix) {
        Category entity = new Category();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCategoryType(converter.fromRow(row, prefix + "_category_type", CategoryType.class));
        entity.setParentId(converter.fromRow(row, prefix + "_parent_id", Long.class));
        return entity;
    }
}
