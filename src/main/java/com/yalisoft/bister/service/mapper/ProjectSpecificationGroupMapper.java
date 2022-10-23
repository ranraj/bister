package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectSpecificationGroup;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.ProjectSpecificationGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectSpecificationGroup} and its DTO {@link ProjectSpecificationGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectSpecificationGroupMapper extends EntityMapper<ProjectSpecificationGroupDTO, ProjectSpecificationGroup> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    ProjectSpecificationGroupDTO toDto(ProjectSpecificationGroup s);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);
}
