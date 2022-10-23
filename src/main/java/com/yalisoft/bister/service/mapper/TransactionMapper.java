package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.domain.Transaction;
import com.yalisoft.bister.service.dto.PurchaseOrderDTO;
import com.yalisoft.bister.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "purchaseOrder", source = "purchaseOrder", qualifiedByName = "purchaseOrderId")
    TransactionDTO toDto(Transaction s);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
