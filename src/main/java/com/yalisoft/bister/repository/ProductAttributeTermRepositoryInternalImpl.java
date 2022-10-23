package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.ProductAttributeTerm;
import com.yalisoft.bister.repository.rowmapper.ProductAttributeRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductAttributeTermRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProductAttributeTerm entity.
 */
@SuppressWarnings("unused")
class ProductAttributeTermRepositoryInternalImpl
    extends SimpleR2dbcRepository<ProductAttributeTerm, Long>
    implements ProductAttributeTermRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductAttributeRowMapper productattributeMapper;
    private final ProductRowMapper productMapper;
    private final ProductAttributeTermRowMapper productattributetermMapper;

    private static final Table entityTable = Table.aliased("product_attribute_term", EntityManager.ENTITY_ALIAS);
    private static final Table productAttributeTable = Table.aliased("product_attribute", "productAttribute");
    private static final Table productTable = Table.aliased("product", "product");

    public ProductAttributeTermRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductAttributeRowMapper productattributeMapper,
        ProductRowMapper productMapper,
        ProductAttributeTermRowMapper productattributetermMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ProductAttributeTerm.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productattributeMapper = productattributeMapper;
        this.productMapper = productMapper;
        this.productattributetermMapper = productattributetermMapper;
    }

    @Override
    public Flux<ProductAttributeTerm> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProductAttributeTerm> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductAttributeTermSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductAttributeSqlHelper.getColumns(productAttributeTable, "productAttribute"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productAttributeTable)
            .on(Column.create("product_attribute_id", entityTable))
            .equals(Column.create("id", productAttributeTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProductAttributeTerm.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProductAttributeTerm> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProductAttributeTerm> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ProductAttributeTerm> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ProductAttributeTerm> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ProductAttributeTerm> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ProductAttributeTerm process(Row row, RowMetadata metadata) {
        ProductAttributeTerm entity = productattributetermMapper.apply(row, "e");
        entity.setProductAttribute(productattributeMapper.apply(row, "productAttribute"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends ProductAttributeTerm> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
