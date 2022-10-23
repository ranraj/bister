package com.yalisoft.bister.service.mapper;

import com.yalisoft.bister.domain.Category;
import com.yalisoft.bister.domain.Product;
import com.yalisoft.bister.domain.Project;
import com.yalisoft.bister.domain.TaxClass;
import com.yalisoft.bister.service.dto.CategoryDTO;
import com.yalisoft.bister.service.dto.ProductDTO;
import com.yalisoft.bister.service.dto.ProjectDTO;
import com.yalisoft.bister.service.dto.TaxClassDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectName")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "categoryNameSet")
    @Mapping(target = "taxClass", source = "taxClass", qualifiedByName = "taxClassName")
    ProductDTO toDto(Product s);

    @Mapping(target = "removeCategory", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("projectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProjectDTO toDtoProjectName(Project project);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("categoryNameSet")
    default Set<CategoryDTO> toDtoCategoryNameSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryName).collect(Collectors.toSet());
    }

    @Named("taxClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TaxClassDTO toDtoTaxClassName(TaxClass taxClass);
}
