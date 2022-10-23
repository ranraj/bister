package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Address;
import com.yalisoft.bister.domain.BusinessPartner;
import com.yalisoft.bister.domain.Organisation;
import com.yalisoft.bister.service.dto.AddressDTO;
import com.yalisoft.bister.service.dto.BusinessPartnerDTO;
import com.yalisoft.bister.service.dto.OrganisationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Organisation} and its DTO {@link OrganisationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganisationMapper extends EntityMapper<OrganisationDTO, Organisation> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressName")
    @Mapping(target = "businessPartner", source = "businessPartner", qualifiedByName = "businessPartnerName")
    OrganisationDTO toDto(Organisation s);

    @Named("addressName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AddressDTO toDtoAddressName(Address address);

    @Named("businessPartnerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BusinessPartnerDTO toDtoBusinessPartnerName(BusinessPartner businessPartner);
}
