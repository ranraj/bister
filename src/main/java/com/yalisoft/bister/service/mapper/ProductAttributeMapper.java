package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.ProductAttribute;
import com.yalisoft.bister.service.dto.ProductAttributeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductAttribute} and its DTO {@link ProductAttributeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductAttributeMapper extends EntityMapper<ProductAttributeDTO, ProductAttribute> {}
