package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.enumeration.FacilityType;
import com.yalisoft.bister.repository.rowmapper.AddressRowMapper;
import com.yalisoft.bister.repository.rowmapper.FacilityRowMapper;
import com.yalisoft.bister.repository.rowmapper.OrganisationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Facility entity.
 */
@SuppressWarnings("unused")
class FacilityRepositoryInternalImpl extends SimpleR2dbcRepository<Facility, Long> implements FacilityRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AddressRowMapper addressMapper;
    private final UserRowMapper userMapper;
    private final OrganisationRowMapper organisationMapper;
    private final FacilityRowMapper facilityMapper;

    private static final Table entityTable = Table.aliased("facility", EntityManager.ENTITY_ALIAS);
    private static final Table addressTable = Table.aliased("address", "address");
    private static final Table userTable = Table.aliased("yali_user", "e_user");
    private static final Table organisationTable = Table.aliased("organisation", "organisation");

    public FacilityRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AddressRowMapper addressMapper,
        UserRowMapper userMapper,
        OrganisationRowMapper organisationMapper,
        FacilityRowMapper facilityMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Facility.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
        this.organisationMapper = organisationMapper;
        this.facilityMapper = facilityMapper;
    }

    @Override
    public Flux<Facility> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Facility> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FacilitySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AddressSqlHelper.getColumns(addressTable, "address"));
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(OrganisationSqlHelper.getColumns(organisationTable, "organisation"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(addressTable)
            .on(Column.create("address_id", entityTable))
            .equals(Column.create("id", addressTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(organisationTable)
            .on(Column.create("organisation_id", entityTable))
            .equals(Column.create("id", organisationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Facility.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Facility> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Facility> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Facility> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Facility> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Facility> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Facility process(Row row, RowMetadata metadata) {
        Facility entity = facilityMapper.apply(row, "e");
        entity.setAddress(addressMapper.apply(row, "address"));
        entity.setUser(userMapper.apply(row, "user"));
        entity.setOrganisation(organisationMapper.apply(row, "organisation"));
        return entity;
    }

    @Override
    public <S extends Facility> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
