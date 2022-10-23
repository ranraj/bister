package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.service.dto.AddressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {}
