package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectSpecification;
import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.ProjectSpecificationDTO;
import com.yalisoft.bister.service.dto.ProjectSpecificationGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectSpecification} and its DTO {@link ProjectSpecificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectSpecificationMapper extends EntityMapper<ProjectSpecificationDTO, ProjectSpecification> {
    @Mapping(target = "projectSpecificationGroup", source = "projectSpecificationGroup", qualifiedByName = "projectSpecificationGroupTitle")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    ProjectSpecificationDTO toDto(ProjectSpecification s);

    @Named("projectSpecificationGroupTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ProjectSpecificationGroupDTO toDtoProjectSpecificationGroupTitle(ProjectSpecificationGroup projectSpecificationGroup);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);
}
