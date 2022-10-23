package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.ProjectType;
import com.yalisoft.bister.service.dto.ProjectTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectType} and its DTO {@link ProjectTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectTypeMapper extends EntityMapper<ProjectTypeDTO, ProjectType> {}
