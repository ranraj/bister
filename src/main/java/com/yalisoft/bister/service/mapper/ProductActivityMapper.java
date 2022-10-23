package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductActivity;
import com.yalisoft.bister.service.dto.ProductActivityDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductActivity} and its DTO {@link ProductActivityDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductActivityMapper extends EntityMapper<ProductActivityDTO, ProductActivity> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductActivityDTO toDto(ProductActivity s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
