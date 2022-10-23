package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariation} and its DTO {@link ProductVariationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductVariationMapper extends EntityMapper<ProductVariationDTO, ProductVariation> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductVariationDTO toDto(ProductVariation s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
