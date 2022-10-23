package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Phonenumber;
import com.yalisoft.bister.domain.enumeration.PhonenumberType;
import com.yalisoft.bister.repository.rowmapper.FacilityRowMapper;
import com.yalisoft.bister.repository.rowmapper.OrganisationRowMapper;
import com.yalisoft.bister.repository.rowmapper.PhonenumberRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Phonenumber entity.
 */
@SuppressWarnings("unused")
class PhonenumberRepositoryInternalImpl extends SimpleR2dbcRepository<Phonenumber, Long> implements PhonenumberRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final OrganisationRowMapper organisationMapper;
    private final FacilityRowMapper facilityMapper;
    private final PhonenumberRowMapper phonenumberMapper;

    private static final Table entityTable = Table.aliased("phonenumber", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("yali_user", "e_user");
    private static final Table organisationTable = Table.aliased("organisation", "organisation");
    private static final Table facilityTable = Table.aliased("facility", "facility");

    public PhonenumberRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        OrganisationRowMapper organisationMapper,
        FacilityRowMapper facilityMapper,
        PhonenumberRowMapper phonenumberMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Phonenumber.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.organisationMapper = organisationMapper;
        this.facilityMapper = facilityMapper;
        this.phonenumberMapper = phonenumberMapper;
    }

    @Override
    public Flux<Phonenumber> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Phonenumber> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PhonenumberSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(OrganisationSqlHelper.getColumns(organisationTable, "organisation"));
        columns.addAll(FacilitySqlHelper.getColumns(facilityTable, "facility"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(organisationTable)
            .on(Column.create("organisation_id", entityTable))
            .equals(Column.create("id", organisationTable))
            .leftOuterJoin(facilityTable)
            .on(Column.create("facility_id", entityTable))
            .equals(Column.create("id", facilityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Phonenumber.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Phonenumber> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Phonenumber> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Phonenumber> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Phonenumber> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Phonenumber> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Phonenumber process(Row row, RowMetadata metadata) {
        Phonenumber entity = phonenumberMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setOrganisation(organisationMapper.apply(row, "organisation"));
        entity.setFacility(facilityMapper.apply(row, "facility"));
        return entity;
    }

    @Override
    public <S extends Phonenumber> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
