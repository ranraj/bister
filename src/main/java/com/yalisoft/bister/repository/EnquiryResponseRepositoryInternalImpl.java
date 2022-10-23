package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.EnquiryResponse;
import com.yalisoft.bister.domain.enumeration.EnquiryResponseType;
import com.yalisoft.bister.repository.rowmapper.AgentRowMapper;
import com.yalisoft.bister.repository.rowmapper.EnquiryResponseRowMapper;
import com.yalisoft.bister.repository.rowmapper.EnquiryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the EnquiryResponse entity.
 */
@SuppressWarnings("unused")
class EnquiryResponseRepositoryInternalImpl
    extends SimpleR2dbcRepository<EnquiryResponse, Long>
    implements EnquiryResponseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AgentRowMapper agentMapper;
    private final EnquiryRowMapper enquiryMapper;
    private final EnquiryResponseRowMapper enquiryresponseMapper;

    private static final Table entityTable = Table.aliased("enquiry_response", EntityManager.ENTITY_ALIAS);
    private static final Table agentTable = Table.aliased("agent", "agent");
    private static final Table enquiryTable = Table.aliased("enquiry", "enquiry");

    public EnquiryResponseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AgentRowMapper agentMapper,
        EnquiryRowMapper enquiryMapper,
        EnquiryResponseRowMapper enquiryresponseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(EnquiryResponse.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.agentMapper = agentMapper;
        this.enquiryMapper = enquiryMapper;
        this.enquiryresponseMapper = enquiryresponseMapper;
    }

    @Override
    public Flux<EnquiryResponse> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<EnquiryResponse> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EnquiryResponseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AgentSqlHelper.getColumns(agentTable, "agent"));
        columns.addAll(EnquirySqlHelper.getColumns(enquiryTable, "enquiry"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(agentTable)
            .on(Column.create("agent_id", entityTable))
            .equals(Column.create("id", agentTable))
            .leftOuterJoin(enquiryTable)
            .on(Column.create("enquiry_id", entityTable))
            .equals(Column.create("id", enquiryTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, EnquiryResponse.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<EnquiryResponse> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<EnquiryResponse> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<EnquiryResponse> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<EnquiryResponse> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<EnquiryResponse> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private EnquiryResponse process(Row row, RowMetadata metadata) {
        EnquiryResponse entity = enquiryresponseMapper.apply(row, "e");
        entity.setAgent(agentMapper.apply(row, "agent"));
        entity.setEnquiry(enquiryMapper.apply(row, "enquiry"));
        return entity;
    }

    @Override
    public <S extends EnquiryResponse> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
