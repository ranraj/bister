package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.Facility;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.dto.FacilityDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityName")
    AgentDTO toDto(Agent s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("facilityName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FacilityDTO toDtoFacilityName(Facility facility);
}
