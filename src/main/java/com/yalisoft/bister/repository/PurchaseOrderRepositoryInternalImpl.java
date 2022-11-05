package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.domain.enumeration.DeliveryOption;
import com.yalisoft.bister.domain.enumeration.OrderStatus;
import com.yalisoft.bister.repository.rowmapper.CustomerRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductVariationRowMapper;
import com.yalisoft.bister.repository.rowmapper.PurchaseOrderRowMapper;
import com.yalisoft.bister.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
 * Spring Data R2DBC custom repository implementation for the PurchaseOrder entity.
 */
@SuppressWarnings("unused")
class PurchaseOrderRepositoryInternalImpl extends SimpleR2dbcRepository<PurchaseOrder, Long> implements PurchaseOrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ProductVariationRowMapper productvariationMapper;
    private final CustomerRowMapper customerMapper;
    private final PurchaseOrderRowMapper purchaseorderMapper;

    private static final Table entityTable = Table.aliased("purchase_order", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("yali_user", "e_user");
    private static final Table productVariationTable = Table.aliased("product_variation", "productVariation");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public PurchaseOrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ProductVariationRowMapper productvariationMapper,
        CustomerRowMapper customerMapper,
        PurchaseOrderRowMapper purchaseorderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PurchaseOrder.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.productvariationMapper = productvariationMapper;
        this.customerMapper = customerMapper;
        this.purchaseorderMapper = purchaseorderMapper;
    }

    @Override
    public Flux<PurchaseOrder> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PurchaseOrder> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PurchaseOrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ProductVariationSqlHelper.getColumns(productVariationTable, "productVariation"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(productVariationTable)
            .on(Column.create("product_variation_id", entityTable))
            .equals(Column.create("id", productVariationTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PurchaseOrder.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PurchaseOrder> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PurchaseOrder> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<PurchaseOrder> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<PurchaseOrder> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<PurchaseOrder> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private PurchaseOrder process(Row row, RowMetadata metadata) {
        PurchaseOrder entity = purchaseorderMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setProductVariation(productvariationMapper.apply(row, "productVariation"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends PurchaseOrder> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
