package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.AddressDTO;
import com.yalisoft.bister.service.dto.FacilityDTO;
import com.yalisoft.bister.service.dto.OrganisationDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Facility} and its DTO {@link FacilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacilityMapper extends EntityMapper<FacilityDTO, Facility> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressName")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "organisation", source = "organisation", qualifiedByName = "organisationName")
    FacilityDTO toDto(Facility s);

    @Named("addressName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AddressDTO toDtoAddressName(Address address);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("organisationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganisationDTO toDtoOrganisationName(Organisation organisation);
}
