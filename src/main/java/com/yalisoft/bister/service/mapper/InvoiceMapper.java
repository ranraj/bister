package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Invoice;
import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.service.dto.InvoiceDTO;
import com.yalisoft.bister.service.dto.PurchaseOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "purchaseOrder", source = "purchaseOrder", qualifiedByName = "purchaseOrderId")
    InvoiceDTO toDto(Invoice s);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
