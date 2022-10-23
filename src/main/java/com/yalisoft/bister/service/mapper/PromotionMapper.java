package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Promotion;
import com.yalisoft.bister.service.dto.PromotionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {}
