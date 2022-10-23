package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.domain.ProductVariationAttributeTerm;
import com.yalisoft.bister.service.dto.ProductVariationAttributeTermDTO;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariationAttributeTerm} and its DTO {@link ProductVariationAttributeTermDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductVariationAttributeTermMapper extends EntityMapper<ProductVariationAttributeTermDTO, ProductVariationAttributeTerm> {
    @Mapping(target = "productVariation", source = "productVariation", qualifiedByName = "productVariationName")
    ProductVariationAttributeTermDTO toDto(ProductVariationAttributeTerm s);

    @Named("productVariationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductVariationDTO toDtoProductVariationName(ProductVariation productVariation);
}
