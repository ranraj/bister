package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.repository.rowmapper.ProjectRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectSpecificationGroupRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProjectSpecificationGroup entity.
 */
@SuppressWarnings("unused")
class ProjectSpecificationGroupRepositoryInternalImpl
    extends SimpleR2dbcRepository<ProjectSpecificationGroup, Long>
    implements ProjectSpecificationGroupRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProjectRowMapper projectMapper;
    private final ProjectSpecificationGroupRowMapper projectspecificationgroupMapper;

    private static final Table entityTable = Table.aliased("project_specification_group", EntityManager.ENTITY_ALIAS);
    private static final Table projectTable = Table.aliased("project", "project");

    public ProjectSpecificationGroupRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProjectRowMapper projectMapper,
        ProjectSpecificationGroupRowMapper projectspecificationgroupMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(
                converter.getMappingContext().getRequiredPersistentEntity(ProjectSpecificationGroup.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.projectMapper = projectMapper;
        this.projectspecificationgroupMapper = projectspecificationgroupMapper;
    }

    @Override
    public Flux<ProjectSpecificationGroup> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProjectSpecificationGroup> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjectSpecificationGroupSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProjectSpecificationGroup.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProjectSpecificationGroup> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProjectSpecificationGroup> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ProjectSpecificationGroup> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ProjectSpecificationGroup> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ProjectSpecificationGroup> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ProjectSpecificationGroup process(Row row, RowMetadata metadata) {
        ProjectSpecificationGroup entity = projectspecificationgroupMapper.apply(row, "e");
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends ProjectSpecificationGroup> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
