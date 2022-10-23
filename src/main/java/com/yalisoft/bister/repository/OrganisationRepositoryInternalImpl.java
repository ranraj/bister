package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.repository.rowmapper.AddressRowMapper;
import com.yalisoft.bister.repository.rowmapper.BusinessPartnerRowMapper;
import com.yalisoft.bister.repository.rowmapper.OrganisationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Organisation entity.
 */
@SuppressWarnings("unused")
class OrganisationRepositoryInternalImpl extends SimpleR2dbcRepository<Organisation, Long> implements OrganisationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AddressRowMapper addressMapper;
    private final BusinessPartnerRowMapper businesspartnerMapper;
    private final OrganisationRowMapper organisationMapper;

    private static final Table entityTable = Table.aliased("organisation", EntityManager.ENTITY_ALIAS);
    private static final Table addressTable = Table.aliased("address", "address");
    private static final Table businessPartnerTable = Table.aliased("business_partner", "businessPartner");

    public OrganisationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AddressRowMapper addressMapper,
        BusinessPartnerRowMapper businesspartnerMapper,
        OrganisationRowMapper organisationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Organisation.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.addressMapper = addressMapper;
        this.businesspartnerMapper = businesspartnerMapper;
        this.organisationMapper = organisationMapper;
    }

    @Override
    public Flux<Organisation> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Organisation> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrganisationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AddressSqlHelper.getColumns(addressTable, "address"));
        columns.addAll(BusinessPartnerSqlHelper.getColumns(businessPartnerTable, "businessPartner"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(addressTable)
            .on(Column.create("address_id", entityTable))
            .equals(Column.create("id", addressTable))
            .leftOuterJoin(businessPartnerTable)
            .on(Column.create("business_partner_id", entityTable))
            .equals(Column.create("id", businessPartnerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Organisation.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Organisation> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Organisation> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Organisation> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Organisation> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Organisation> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Organisation process(Row row, RowMetadata metadata) {
        Organisation entity = organisationMapper.apply(row, "e");
        entity.setAddress(addressMapper.apply(row, "address"));
        entity.setBusinessPartner(businesspartnerMapper.apply(row, "businessPartner"));
        return entity;
    }

    @Override
    public <S extends Organisation> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
