package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductSpecification;
import com.yalisoft.bister.domain.ProductSpecificationGroup;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProductSpecificationDTO;
import com.yalisoft.bister.service.dto.ProductSpecificationGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductSpecification} and its DTO {@link ProductSpecificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductSpecificationMapper extends EntityMapper<ProductSpecificationDTO, ProductSpecification> {
    @Mapping(target = "productSpecificationGroup", source = "productSpecificationGroup", qualifiedByName = "productSpecificationGroupTitle")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductSpecificationDTO toDto(ProductSpecification s);

    @Named("productSpecificationGroupTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ProductSpecificationGroupDTO toDtoProductSpecificationGroupTitle(ProductSpecificationGroup productSpecificationGroup);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
