package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.domain.TaxRate;
import com.yalisoft.bister.service.dto.TaxClassDTO;
import com.yalisoft.bister.service.dto.TaxRateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaxRate} and its DTO {@link TaxRateDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaxRateMapper extends EntityMapper<TaxRateDTO, TaxRate> {
    @Mapping(target = "taxClass", source = "taxClass", qualifiedByName = "taxClassName")
    TaxRateDTO toDto(TaxRate s);

    @Named("taxClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TaxClassDTO toDtoTaxClassName(TaxClass taxClass);
}
