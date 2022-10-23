package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Refund;
import com.yalisoft.bister.domain.enumeration.RefundStatus;
import com.yalisoft.bister.repository.rowmapper.RefundRowMapper;
import com.yalisoft.bister.repository.rowmapper.TransactionRowMapper;
import com.yalisoft.bister.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
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
 * Spring Data R2DBC custom repository implementation for the Refund entity.
 */
@SuppressWarnings("unused")
class RefundRepositoryInternalImpl extends SimpleR2dbcRepository<Refund, Long> implements RefundRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TransactionRowMapper transactionMapper;
    private final UserRowMapper userMapper;
    private final RefundRowMapper refundMapper;

    private static final Table entityTable = Table.aliased("refund", EntityManager.ENTITY_ALIAS);
    private static final Table transactionTable = Table.aliased("transaction", "e_transaction");
    private static final Table userTable = Table.aliased("yali_user", "e_user");

    public RefundRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TransactionRowMapper transactionMapper,
        UserRowMapper userMapper,
        RefundRowMapper refundMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Refund.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.transactionMapper = transactionMapper;
        this.userMapper = userMapper;
        this.refundMapper = refundMapper;
    }

    @Override
    public Flux<Refund> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Refund> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RefundSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TransactionSqlHelper.getColumns(transactionTable, "transaction"));
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(transactionTable)
            .on(Column.create("transaction_id", entityTable))
            .equals(Column.create("id", transactionTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Refund.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Refund> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Refund> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Refund> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Refund> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Refund> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Refund process(Row row, RowMetadata metadata) {
        Refund entity = refundMapper.apply(row, "e");
        entity.setTransaction(transactionMapper.apply(row, "transaction"));
        entity.setUser(userMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends Refund> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
