package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.service.dto.TaxClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaxClass} and its DTO {@link TaxClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaxClassMapper extends EntityMapper<TaxClassDTO, TaxClass> {}
