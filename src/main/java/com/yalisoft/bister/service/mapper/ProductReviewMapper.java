package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.ProductReview;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProductReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductReview} and its DTO {@link ProductReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductReviewMapper extends EntityMapper<ProductReviewDTO, ProductReview> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductReviewDTO toDto(ProductReview s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
