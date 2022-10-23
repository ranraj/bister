package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import com.yalisoft.bister.repository.rowmapper.ProductVariationAttributeTermRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductVariationRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ProductVariationAttributeTerm entity.
 */
@SuppressWarnings("unused")
class ProductVariationAttributeTermRepositoryInternalImpl
    extends SimpleR2dbcRepository<ProductVariationAttributeTerm, Long>
    implements ProductVariationAttributeTermRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductVariationRowMapper productvariationMapper;
    private final ProductVariationAttributeTermRowMapper productvariationattributetermMapper;

    private static final Table entityTable = Table.aliased("product_variation_attribute_term", EntityManager.ENTITY_ALIAS);
    private static final Table productVariationTable = Table.aliased("product_variation", "productVariation");

    public ProductVariationAttributeTermRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductVariationRowMapper productvariationMapper,
        ProductVariationAttributeTermRowMapper productvariationattributetermMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(
                converter.getMappingContext().getRequiredPersistentEntity(ProductVariationAttributeTerm.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productvariationMapper = productvariationMapper;
        this.productvariationattributetermMapper = productvariationattributetermMapper;
    }

    @Override
    public Flux<ProductVariationAttributeTerm> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProductVariationAttributeTerm> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductVariationAttributeTermSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductVariationSqlHelper.getColumns(productVariationTable, "productVariation"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productVariationTable)
            .on(Column.create("product_variation_id", entityTable))
            .equals(Column.create("id", productVariationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProductVariationAttributeTerm.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProductVariationAttributeTerm> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProductVariationAttributeTerm> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ProductVariationAttributeTerm> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ProductVariationAttributeTerm> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ProductVariationAttributeTerm process(Row row, RowMetadata metadata) {
        ProductVariationAttributeTerm entity = productvariationattributetermMapper.apply(row, "e");
        entity.setProductVariation(productvariationMapper.apply(row, "productVariation"));
        return entity;
    }

    @Override
    public <S extends ProductVariationAttributeTerm> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
