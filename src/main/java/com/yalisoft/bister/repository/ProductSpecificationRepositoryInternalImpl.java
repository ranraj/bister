package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.ProductSpecification;
import com.yalisoft.bister.repository.rowmapper.ProductRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductSpecificationGroupRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductSpecificationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProductSpecification entity.
 */
@SuppressWarnings("unused")
class ProductSpecificationRepositoryInternalImpl
    extends SimpleR2dbcRepository<ProductSpecification, Long>
    implements ProductSpecificationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductSpecificationGroupRowMapper productspecificationgroupMapper;
    private final ProductRowMapper productMapper;
    private final ProductSpecificationRowMapper productspecificationMapper;

    private static final Table entityTable = Table.aliased("product_specification", EntityManager.ENTITY_ALIAS);
    private static final Table productSpecificationGroupTable = Table.aliased("product_specification_group", "productSpecificationGroup");
    private static final Table productTable = Table.aliased("product", "product");

    public ProductSpecificationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductSpecificationGroupRowMapper productspecificationgroupMapper,
        ProductRowMapper productMapper,
        ProductSpecificationRowMapper productspecificationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ProductSpecification.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productspecificationgroupMapper = productspecificationgroupMapper;
        this.productMapper = productMapper;
        this.productspecificationMapper = productspecificationMapper;
    }

    @Override
    public Flux<ProductSpecification> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProductSpecification> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductSpecificationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSpecificationGroupSqlHelper.getColumns(productSpecificationGroupTable, "productSpecificationGroup"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productSpecificationGroupTable)
            .on(Column.create("product_specification_group_id", entityTable))
            .equals(Column.create("id", productSpecificationGroupTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProductSpecification.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProductSpecification> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProductSpecification> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ProductSpecification> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ProductSpecification> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ProductSpecification> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ProductSpecification process(Row row, RowMetadata metadata) {
        ProductSpecification entity = productspecificationMapper.apply(row, "e");
        entity.setProductSpecificationGroup(productspecificationgroupMapper.apply(row, "productSpecificationGroup"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends ProductSpecification> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
