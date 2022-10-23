package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.enumeration.AgentType;
import com.yalisoft.bister.repository.rowmapper.AgentRowMapper;
import com.yalisoft.bister.repository.rowmapper.FacilityRowMapper;
import com.yalisoft.bister.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Agent entity.
 */
@SuppressWarnings("unused")
class AgentRepositoryInternalImpl extends SimpleR2dbcRepository<Agent, Long> implements AgentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final FacilityRowMapper facilityMapper;
    private final AgentRowMapper agentMapper;

    private static final Table entityTable = Table.aliased("agent", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("yali_user", "e_user");
    private static final Table facilityTable = Table.aliased("facility", "facility");

    public AgentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        FacilityRowMapper facilityMapper,
        AgentRowMapper agentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Agent.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.facilityMapper = facilityMapper;
        this.agentMapper = agentMapper;
    }

    @Override
    public Flux<Agent> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Agent> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AgentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(FacilitySqlHelper.getColumns(facilityTable, "facility"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(facilityTable)
            .on(Column.create("facility_id", entityTable))
            .equals(Column.create("id", facilityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Agent.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Agent> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Agent> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Agent> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Agent> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Agent> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Agent process(Row row, RowMetadata metadata) {
        Agent entity = agentMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setFacility(facilityMapper.apply(row, "facility"));
        return entity;
    }

    @Override
    public <S extends Agent> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
