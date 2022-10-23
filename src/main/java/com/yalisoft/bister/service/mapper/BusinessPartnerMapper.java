package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.BusinessPartner;
import com.yalisoft.bister.service.dto.BusinessPartnerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BusinessPartner} and its DTO {@link BusinessPartnerDTO}.
 */
@Mapper(componentModel = "spring")
public interface BusinessPartnerMapper extends EntityMapper<BusinessPartnerDTO, BusinessPartner> {}
