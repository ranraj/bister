package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.Notification;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.dto.NotificationDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    NotificationDTO toDto(Notification s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
