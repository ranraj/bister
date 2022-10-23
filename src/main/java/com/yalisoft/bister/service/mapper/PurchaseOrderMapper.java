package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import com.yalisoft.bister.service.dto.PurchaseOrderDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrder} and its DTO {@link PurchaseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper extends EntityMapper<PurchaseOrderDTO, PurchaseOrder> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "productVariation", source = "productVariation", qualifiedByName = "productVariationName")
    PurchaseOrderDTO toDto(PurchaseOrder s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("productVariationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductVariationDTO toDtoProductVariationName(ProductVariation productVariation);
}
