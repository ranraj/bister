package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Attachment;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.Tag;
import com.yalisoft.bister.service.dto.AttachmentDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.TagDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "attachmentName")
    TagDTO toDto(Tag s);

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

    @Named("attachmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AttachmentDTO toDtoAttachmentName(Attachment attachment);
}
