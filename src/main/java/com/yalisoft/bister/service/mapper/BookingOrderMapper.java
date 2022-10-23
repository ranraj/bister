package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.BookingOrder;
import com.yalisoft.bister.domain.Customer;
import com.yalisoft.bister.domain.ProductVariation;
import com.yalisoft.bister.service.dto.BookingOrderDTO;
import com.yalisoft.bister.service.dto.CustomerDTO;
import com.yalisoft.bister.service.dto.ProductVariationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookingOrder} and its DTO {@link BookingOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingOrderMapper extends EntityMapper<BookingOrderDTO, BookingOrder> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    @Mapping(target = "productVariation", source = "productVariation", qualifiedByName = "productVariationName")
    BookingOrderDTO toDto(BookingOrder s);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);

    @Named("productVariationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductVariationDTO toDtoProductVariationName(ProductVariation productVariation);
}
