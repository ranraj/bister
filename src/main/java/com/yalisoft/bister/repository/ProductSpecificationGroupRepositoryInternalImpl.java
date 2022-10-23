package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.ProductSpecificationGroup;
import com.yalisoft.bister.repository.rowmapper.ProductRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductSpecificationGroupRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProductSpecificationGroup entity.
 */
@SuppressWarnings("unused")
class ProductSpecificationGroupRepositoryInternalImpl
    extends SimpleR2dbcRepository<ProductSpecificationGroup, Long>
    implements ProductSpecificationGroupRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductRowMapper productMapper;
    private final ProductSpecificationGroupRowMapper productspecificationgroupMapper;

    private static final Table entityTable = Table.aliased("product_specification_group", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("product", "product");

    public ProductSpecificationGroupRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductRowMapper productMapper,
        ProductSpecificationGroupRowMapper productspecificationgroupMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(
                converter.getMappingContext().getRequiredPersistentEntity(ProductSpecificationGroup.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productMapper = productMapper;
        this.productspecificationgroupMapper = productspecificationgroupMapper;
    }

    @Override
    public Flux<ProductSpecificationGroup> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProductSpecificationGroup> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductSpecificationGroupSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProductSpecificationGroup.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProductSpecificationGroup> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProductSpecificationGroup> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ProductSpecificationGroup> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ProductSpecificationGroup> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ProductSpecificationGroup> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ProductSpecificationGroup process(Row row, RowMetadata metadata) {
        ProductSpecificationGroup entity = productspecificationgroupMapper.apply(row, "e");
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends ProductSpecificationGroup> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
