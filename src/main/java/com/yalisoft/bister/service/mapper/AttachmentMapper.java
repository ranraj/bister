package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.domain.Certification;
import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductSpecification;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectSpecification;
import com.yalisoft.bister.service.dto.AttachmentDTO;
import com.yalisoft.bister.service.dto.CertificationDTO;
import com.yalisoft.bister.service.dto.EnquiryDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProductSpecificationDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.ProjectSpecificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    @Mapping(target = "enquiry", source = "enquiry", qualifiedByName = "enquirySubject")
    @Mapping(target = "certification", source = "certification", qualifiedByName = "certificationName")
    @Mapping(target = "productSpecification", source = "productSpecification", qualifiedByName = "productSpecificationTitle")
    @Mapping(target = "projectSpecification", source = "projectSpecification", qualifiedByName = "projectSpecificationTitle")
    AttachmentDTO toDto(Attachment s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);

    @Named("enquirySubject")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "subject", source = "subject")
    EnquiryDTO toDtoEnquirySubject(Enquiry enquiry);

    @Named("certificationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CertificationDTO toDtoCertificationName(Certification certification);

    @Named("productSpecificationTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ProductSpecificationDTO toDtoProductSpecificationTitle(ProductSpecification productSpecification);

    @Named("projectSpecificationTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ProjectSpecificationDTO toDtoProjectSpecificationTitle(ProjectSpecification projectSpecification);
}
