package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.enumeration.EnquiryResolutionStatus;
import com.yalisoft.bister.domain.enumeration.EnquiryType;
import com.yalisoft.bister.repository.rowmapper.AgentRowMapper;
import com.yalisoft.bister.repository.rowmapper.CustomerRowMapper;
import com.yalisoft.bister.repository.rowmapper.EnquiryRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Enquiry entity.
 */
@SuppressWarnings("unused")
class EnquiryRepositoryInternalImpl extends SimpleR2dbcRepository<Enquiry, Long> implements EnquiryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AgentRowMapper agentMapper;
    private final ProjectRowMapper projectMapper;
    private final ProductRowMapper productMapper;
    private final CustomerRowMapper customerMapper;
    private final EnquiryRowMapper enquiryMapper;

    private static final Table entityTable = Table.aliased("enquiry", EntityManager.ENTITY_ALIAS);
    private static final Table agentTable = Table.aliased("agent", "agent");
    private static final Table projectTable = Table.aliased("project", "project");
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public EnquiryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AgentRowMapper agentMapper,
        ProjectRowMapper projectMapper,
        ProductRowMapper productMapper,
        CustomerRowMapper customerMapper,
        EnquiryRowMapper enquiryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Enquiry.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.agentMapper = agentMapper;
        this.projectMapper = projectMapper;
        this.productMapper = productMapper;
        this.customerMapper = customerMapper;
        this.enquiryMapper = enquiryMapper;
    }

    @Override
    public Flux<Enquiry> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Enquiry> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EnquirySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AgentSqlHelper.getColumns(agentTable, "agent"));
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(agentTable)
            .on(Column.create("agent_id", entityTable))
            .equals(Column.create("id", agentTable))
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Enquiry.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Enquiry> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Enquiry> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Enquiry> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Enquiry> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Enquiry> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Enquiry process(Row row, RowMetadata metadata) {
        Enquiry entity = enquiryMapper.apply(row, "e");
        entity.setAgent(agentMapper.apply(row, "agent"));
        entity.setProject(projectMapper.apply(row, "project"));
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Enquiry> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
