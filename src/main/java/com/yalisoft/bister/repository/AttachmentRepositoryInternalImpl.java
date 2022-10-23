package com.yalisoft.bister.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.domain.enumeration.AttachmentApprovalStatus;
import com.yalisoft.bister.domain.enumeration.AttachmentSourceType;
import com.yalisoft.bister.domain.enumeration.AttachmentType;
import com.yalisoft.bister.domain.enumeration.AttachmentVisibilityType;
import com.yalisoft.bister.repository.rowmapper.AttachmentRowMapper;
import com.yalisoft.bister.repository.rowmapper.CertificationRowMapper;
import com.yalisoft.bister.repository.rowmapper.EnquiryRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProductSpecificationRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectRowMapper;
import com.yalisoft.bister.repository.rowmapper.ProjectSpecificationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Attachment entity.
 */
@SuppressWarnings("unused")
class AttachmentRepositoryInternalImpl extends SimpleR2dbcRepository<Attachment, Long> implements AttachmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductRowMapper productMapper;
    private final ProjectRowMapper projectMapper;
    private final EnquiryRowMapper enquiryMapper;
    private final CertificationRowMapper certificationMapper;
    private final ProductSpecificationRowMapper productspecificationMapper;
    private final ProjectSpecificationRowMapper projectspecificationMapper;
    private final AttachmentRowMapper attachmentMapper;

    private static final Table entityTable = Table.aliased("attachment", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table projectTable = Table.aliased("project", "project");
    private static final Table enquiryTable = Table.aliased("enquiry", "enquiry");
    private static final Table certificationTable = Table.aliased("certification", "certification");
    private static final Table productSpecificationTable = Table.aliased("product_specification", "productSpecification");
    private static final Table projectSpecificationTable = Table.aliased("project_specification", "projectSpecification");

    public AttachmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductRowMapper productMapper,
        ProjectRowMapper projectMapper,
        EnquiryRowMapper enquiryMapper,
        CertificationRowMapper certificationMapper,
        ProductSpecificationRowMapper productspecificationMapper,
        ProjectSpecificationRowMapper projectspecificationMapper,
        AttachmentRowMapper attachmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Attachment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productMapper = productMapper;
        this.projectMapper = projectMapper;
        this.enquiryMapper = enquiryMapper;
        this.certificationMapper = certificationMapper;
        this.productspecificationMapper = productspecificationMapper;
        this.projectspecificationMapper = projectspecificationMapper;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public Flux<Attachment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Attachment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AttachmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        columns.addAll(EnquirySqlHelper.getColumns(enquiryTable, "enquiry"));
        columns.addAll(CertificationSqlHelper.getColumns(certificationTable, "certification"));
        columns.addAll(ProductSpecificationSqlHelper.getColumns(productSpecificationTable, "productSpecification"));
        columns.addAll(ProjectSpecificationSqlHelper.getColumns(projectSpecificationTable, "projectSpecification"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable))
            .leftOuterJoin(enquiryTable)
            .on(Column.create("enquiry_id", entityTable))
            .equals(Column.create("id", enquiryTable))
            .leftOuterJoin(certificationTable)
            .on(Column.create("certification_id", entityTable))
            .equals(Column.create("id", certificationTable))
            .leftOuterJoin(productSpecificationTable)
            .on(Column.create("product_specification_id", entityTable))
            .equals(Column.create("id", productSpecificationTable))
            .leftOuterJoin(projectSpecificationTable)
            .on(Column.create("project_specification_id", entityTable))
            .equals(Column.create("id", projectSpecificationTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Attachment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Attachment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Attachment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Attachment> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Attachment> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Attachment> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Attachment process(Row row, RowMetadata metadata) {
        Attachment entity = attachmentMapper.apply(row, "e");
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setProject(projectMapper.apply(row, "project"));
        entity.setEnquiry(enquiryMapper.apply(row, "enquiry"));
        entity.setCertification(certificationMapper.apply(row, "certification"));
        entity.setProductSpecification(productspecificationMapper.apply(row, "productSpecification"));
        entity.setProjectSpecification(projectspecificationMapper.apply(row, "projectSpecification"));
        return entity;
    }

    @Override
    public <S extends Attachment> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
