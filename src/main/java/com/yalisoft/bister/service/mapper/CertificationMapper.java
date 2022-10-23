package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Certification;
import com.yalisoft.bister.service.dto.CertificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Certification} and its DTO {@link CertificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface CertificationMapper extends EntityMapper<CertificationDTO, Certification> {}
