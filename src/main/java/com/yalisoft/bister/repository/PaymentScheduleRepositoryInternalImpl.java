package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.domain.enumeration.PaymentScheduleStatus;
import com.yalisoft.bister.repository.rowmapper.InvoiceRowMapper;
import com.yalisoft.bister.repository.rowmapper.PaymentScheduleRowMapper;
import com.yalisoft.bister.repository.rowmapper.PurchaseOrderRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
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
 * Spring Data R2DBC custom repository implementation for the PaymentSchedule entity.
 */
@SuppressWarnings("unused")
class PaymentScheduleRepositoryInternalImpl
    extends SimpleR2dbcRepository<PaymentSchedule, Long>
    implements PaymentScheduleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InvoiceRowMapper invoiceMapper;
    private final PurchaseOrderRowMapper purchaseorderMapper;
    private final PaymentScheduleRowMapper paymentscheduleMapper;

    private static final Table entityTable = Table.aliased("payment_schedule", EntityManager.ENTITY_ALIAS);
    private static final Table invoiceTable = Table.aliased("invoice", "invoice");
    private static final Table purchaseOrdepTable = Table.aliased("purchase_order", "purchaseOrdep");

    public PaymentScheduleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InvoiceRowMapper invoiceMapper,
        PurchaseOrderRowMapper purchaseorderMapper,
        PaymentScheduleRowMapper paymentscheduleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PaymentSchedule.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.invoiceMapper = invoiceMapper;
        this.purchaseorderMapper = purchaseorderMapper;
        this.paymentscheduleMapper = paymentscheduleMapper;
    }

    @Override
    public Flux<PaymentSchedule> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PaymentSchedule> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PaymentScheduleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InvoiceSqlHelper.getColumns(invoiceTable, "invoice"));
        columns.addAll(PurchaseOrderSqlHelper.getColumns(purchaseOrdepTable, "purchaseOrdep"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(invoiceTable)
            .on(Column.create("invoice_id", entityTable))
            .equals(Column.create("id", invoiceTable))
            .leftOuterJoin(purchaseOrdepTable)
            .on(Column.create("purchase_ordep_id", entityTable))
            .equals(Column.create("id", purchaseOrdepTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PaymentSchedule.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PaymentSchedule> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PaymentSchedule> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private PaymentSchedule process(Row row, RowMetadata metadata) {
        PaymentSchedule entity = paymentscheduleMapper.apply(row, "e");
        entity.setInvoice(invoiceMapper.apply(row, "invoice"));
        entity.setPurchaseOrdep(purchaseorderMapper.apply(row, "purchaseOrdep"));
        return entity;
    }

    @Override
    public <S extends PaymentSchedule> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
