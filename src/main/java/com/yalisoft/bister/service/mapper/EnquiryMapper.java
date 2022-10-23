package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Agent;
import com.yalisoft.bister.domain.Customer;
import com.yalisoft.bister.domain.Enquiry;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.service.dto.AgentDTO;
import com.yalisoft.bister.service.dto.CustomerDTO;
import com.yalisoft.bister.service.dto.EnquiryDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Enquiry} and its DTO {@link EnquiryDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnquiryMapper extends EntityMapper<EnquiryDTO, Enquiry> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentName")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    EnquiryDTO toDto(Enquiry s);

    @Named("agentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AgentDTO toDtoAgentName(Agent agent);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);
}
