package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductSpecificationGroup;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProductSpecificationGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductSpecificationGroup} and its DTO {@link ProductSpecificationGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductSpecificationGroupMapper extends EntityMapper<ProductSpecificationGroupDTO, ProductSpecificationGroup> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductSpecificationGroupDTO toDto(ProductSpecificationGroup s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
