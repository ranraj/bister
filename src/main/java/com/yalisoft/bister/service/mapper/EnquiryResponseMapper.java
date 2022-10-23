package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.EnquiryResponse;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.dto.EnquiryDTO;
import com.yalisoft.bister.service.dto.EnquiryResponseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnquiryResponse} and its DTO {@link EnquiryResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnquiryResponseMapper extends EntityMapper<EnquiryResponseDTO, EnquiryResponse> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentName")
    @Mapping(target = "enquiry", source = "enquiry", qualifiedByName = "enquiryId")
    EnquiryResponseDTO toDto(EnquiryResponse s);

    @Named("agentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AgentDTO toDtoAgentName(Agent agent);

    @Named("enquiryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnquiryDTO toDtoEnquiryId(Enquiry enquiry);
}
