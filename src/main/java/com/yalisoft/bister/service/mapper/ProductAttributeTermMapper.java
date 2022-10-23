package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductAttribute;
import com.yalisoft.bister.domain.ProductAttributeTerm;
import com.yalisoft.bister.service.dto.ProductAttributeDTO;
import com.yalisoft.bister.service.dto.ProductAttributeTermDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductAttributeTerm} and its DTO {@link ProductAttributeTermDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductAttributeTermMapper extends EntityMapper<ProductAttributeTermDTO, ProductAttributeTerm> {
    @Mapping(target = "productAttribute", source = "productAttribute", qualifiedByName = "productAttributeName")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductAttributeTermDTO toDto(ProductAttributeTerm s);

    @Named("productAttributeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductAttributeDTO toDtoProductAttributeName(ProductAttribute productAttribute);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
