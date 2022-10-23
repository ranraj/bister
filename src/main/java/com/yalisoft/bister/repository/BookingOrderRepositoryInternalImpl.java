package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.BookingOrder;
import com.yalisoft.bister.domain.enumeration.BookingOrderStatus;
import com.yalisoft.bister.repository.rowmapper.BookingOrderRowMapper;
import com.yalisoft.bister.repository.rowmapper.CustomerRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductVariationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the BookingOrder entity.
 */
@SuppressWarnings("unused")
class BookingOrderRepositoryInternalImpl extends SimpleR2dbcRepository<BookingOrder, Long> implements BookingOrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final ProductVariationRowMapper productvariationMapper;
    private final BookingOrderRowMapper bookingorderMapper;

    private static final Table entityTable = Table.aliased("booking_order", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table productVariationTable = Table.aliased("product_variation", "productVariation");

    public BookingOrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        ProductVariationRowMapper productvariationMapper,
        BookingOrderRowMapper bookingorderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BookingOrder.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.productvariationMapper = productvariationMapper;
        this.bookingorderMapper = bookingorderMapper;
    }

    @Override
    public Flux<BookingOrder> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<BookingOrder> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BookingOrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(ProductVariationSqlHelper.getColumns(productVariationTable, "productVariation"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(productVariationTable)
            .on(Column.create("product_variation_id", entityTable))
            .equals(Column.create("id", productVariationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, BookingOrder.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BookingOrder> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<BookingOrder> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<BookingOrder> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<BookingOrder> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<BookingOrder> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private BookingOrder process(Row row, RowMetadata metadata) {
        BookingOrder entity = bookingorderMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setProductVariation(productvariationMapper.apply(row, "productVariation"));
        return entity;
    }

    @Override
    public <S extends BookingOrder> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
