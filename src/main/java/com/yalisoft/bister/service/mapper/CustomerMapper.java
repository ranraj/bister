package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.Customer;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.AddressDTO;
import com.yalisoft.bister.service.dto.CustomerDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressName")
    CustomerDTO toDto(Customer s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("addressName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AddressDTO toDtoAddressName(Address address);
}
