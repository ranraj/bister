package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.TaxRate;
import com.yalisoft.bister.repository.rowmapper.TaxClassRowMapper;
import com.yalisoft.bister.repository.rowmapper.TaxRateRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TaxRate entity.
 */
@SuppressWarnings("unused")
class TaxRateRepositoryInternalImpl extends SimpleR2dbcRepository<TaxRate, Long> implements TaxRateRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TaxClassRowMapper taxclassMapper;
    private final TaxRateRowMapper taxrateMapper;

    private static final Table entityTable = Table.aliased("tax_rate", EntityManager.ENTITY_ALIAS);
    private static final Table taxClassTable = Table.aliased("tax_class", "taxClass");

    public TaxRateRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TaxClassRowMapper taxclassMapper,
        TaxRateRowMapper taxrateMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TaxRate.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.taxclassMapper = taxclassMapper;
        this.taxrateMapper = taxrateMapper;
    }

    @Override
    public Flux<TaxRate> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TaxRate> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TaxRateSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TaxClassSqlHelper.getColumns(taxClassTable, "taxClass"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(taxClassTable)
            .on(Column.create("tax_class_id", entityTable))
            .equals(Column.create("id", taxClassTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TaxRate.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TaxRate> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TaxRate> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<TaxRate> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<TaxRate> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<TaxRate> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private TaxRate process(Row row, RowMetadata metadata) {
        TaxRate entity = taxrateMapper.apply(row, "e");
        entity.setTaxClass(taxclassMapper.apply(row, "taxClass"));
        return entity;
    }

    @Override
    public <S extends TaxRate> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
