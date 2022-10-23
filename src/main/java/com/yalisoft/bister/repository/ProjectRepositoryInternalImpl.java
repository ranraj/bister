package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.yalisoft.bister.domain.Category;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.enumeration.ProjectStatus;
import com.yalisoft.bister.repository.rowmapper.AddressRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectTypeRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
 * Spring Data R2DBC custom repository implementation for the Project entity.
 */
@SuppressWarnings("unused")
class ProjectRepositoryInternalImpl extends SimpleR2dbcRepository<Project, Long> implements ProjectRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AddressRowMapper addressMapper;
    private final ProjectTypeRowMapper projecttypeMapper;
    private final ProjectRowMapper projectMapper;

    private static final Table entityTable = Table.aliased("project", EntityManager.ENTITY_ALIAS);
    private static final Table addressTable = Table.aliased("address", "address");
    private static final Table projectTypeTable = Table.aliased("project_type", "projectType");

    private static final EntityManager.LinkTable categoryLink = new EntityManager.LinkTable(
        "rel_project__category",
        "project_id",
        "category_id"
    );

    public ProjectRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AddressRowMapper addressMapper,
        ProjectTypeRowMapper projecttypeMapper,
        ProjectRowMapper projectMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Project.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.addressMapper = addressMapper;
        this.projecttypeMapper = projecttypeMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public Flux<Project> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Project> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AddressSqlHelper.getColumns(addressTable, "address"));
        columns.addAll(ProjectTypeSqlHelper.getColumns(projectTypeTable, "projectType"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(addressTable)
            .on(Column.create("address_id", entityTable))
            .equals(Column.create("id", addressTable))
            .leftOuterJoin(projectTypeTable)
            .on(Column.create("project_type_id", entityTable))
            .equals(Column.create("id", projectTypeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Project.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Project> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Project> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Project> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Project> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Project> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Project process(Row row, RowMetadata metadata) {
        Project entity = projectMapper.apply(row, "e");
        entity.setAddress(addressMapper.apply(row, "address"));
        entity.setProjectType(projecttypeMapper.apply(row, "projectType"));
        return entity;
    }

    @Override
    public <S extends Project> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Project> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(categoryLink, entity.getId(), entity.getCategories().stream().map(Category::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(categoryLink, entityId);
    }
}
