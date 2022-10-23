package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.domain.Phonenumber;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.FacilityDTO;
import com.yalisoft.bister.service.dto.OrganisationDTO;
import com.yalisoft.bister.service.dto.PhonenumberDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Phonenumber} and its DTO {@link PhonenumberDTO}.
 */
@Mapper(componentModel = "spring")
public interface PhonenumberMapper extends EntityMapper<PhonenumberDTO, Phonenumber> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "organisation", source = "organisation", qualifiedByName = "organisationName")
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityName")
    PhonenumberDTO toDto(Phonenumber s);

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

    @Named("facilityName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FacilityDTO toDtoFacilityName(Facility facility);
}
