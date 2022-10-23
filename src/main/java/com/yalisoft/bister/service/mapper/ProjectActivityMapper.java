package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectActivity;
import com.yalisoft.bister.service.dto.ProjectActivityDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectActivity} and its DTO {@link ProjectActivityDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectActivityMapper extends EntityMapper<ProjectActivityDTO, ProjectActivity> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    ProjectActivityDTO toDto(ProjectActivity s);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);
}
