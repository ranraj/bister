package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Invoice;
import com.yalisoft.bister.domain.PaymentSchedule;
import com.yalisoft.bister.domain.PurchaseOrder;
import com.yalisoft.bister.service.dto.InvoiceDTO;
import com.yalisoft.bister.service.dto.PaymentScheduleDTO;
import com.yalisoft.bister.service.dto.PurchaseOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentSchedule} and its DTO {@link PaymentScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentScheduleMapper extends EntityMapper<PaymentScheduleDTO, PaymentSchedule> {
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceId")
    @Mapping(target = "purchaseOrdep", source = "purchaseOrdep", qualifiedByName = "purchaseOrderId")
    PaymentScheduleDTO toDto(PaymentSchedule s);

    @Named("invoiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceDTO toDtoInvoiceId(Invoice invoice);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
