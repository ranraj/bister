package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Refund;
import com.yalisoft.bister.domain.Transaction;
import com.yalisoft.bister.domain.User;
import com.yalisoft.bister.service.dto.RefundDTO;
import com.yalisoft.bister.service.dto.TransactionDTO;
import com.yalisoft.bister.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refund} and its DTO {@link RefundDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundMapper extends EntityMapper<RefundDTO, Refund> {
    @Mapping(target = "transaction", source = "transaction", qualifiedByName = "transactionCode")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    RefundDTO toDto(Refund s);

    @Named("transactionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    TransactionDTO toDtoTransactionCode(Transaction transaction);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
