package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.ProjectReview;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.ProjectReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectReview} and its DTO {@link ProjectReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectReviewMapper extends EntityMapper<ProjectReviewDTO, ProjectReview> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    ProjectReviewDTO toDto(ProjectReview s);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);
}
