package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.Category;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectType;
import com.yalisoft.bister.service.dto.AddressDTO;
import com.yalisoft.bister.service.dto.CategoryDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.ProjectTypeDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressName")
    @Mapping(target = "projectType", source = "projectType", qualifiedByName = "projectTypeName")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "categoryNameSet")
    ProjectDTO toDto(Project s);

    @Mapping(target = "removeCategory", ignore = true)
    Project toEntity(ProjectDTO projectDTO);

    @Named("addressName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AddressDTO toDtoAddressName(Address address);

    @Named("projectTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectTypeDTO toDtoProjectTypeName(ProjectType projectType);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("categoryNameSet")
    default Set<CategoryDTO> toDtoCategoryNameSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryName).collect(Collectors.toSet());
    }
}
