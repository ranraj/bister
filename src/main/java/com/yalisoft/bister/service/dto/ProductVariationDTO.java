package com.yalisoft.bister.service.dto;

import com.yalisoft.bister.domain.enumeration.SaleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.yalisoft.bister.domain.ProductVariation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariationDTO implements Serializable {

    private Long id;

    private String assetId;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 100)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull(message = "must not be null")
    private BigDecimal regularPrice;

    @NotNull(message = "must not be null")
    private BigDecimal salePrice;

    @NotNull(message = "must not be null")
    private LocalDate dateOnSaleFrom;

    @NotNull(message = "must not be null")
    private LocalDate dateOnSaleTo;

    @NotNull(message = "must not be null")
    private Boolean isDraft;

    @NotNull(message = "must not be null")
    private Boolean useParentDetails;

    @NotNull(message = "must not be null")
    private SaleStatus saleStatus;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public LocalDate getDateOnSaleFrom() {
        return dateOnSaleFrom;
    }

    public void setDateOnSaleFrom(LocalDate dateOnSaleFrom) {
        this.dateOnSaleFrom = dateOnSaleFrom;
    }

    public LocalDate getDateOnSaleTo() {
        return dateOnSaleTo;
    }

    public void setDateOnSaleTo(LocalDate dateOnSaleTo) {
        this.dateOnSaleTo = dateOnSaleTo;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public Boolean getUseParentDetails() {
        return useParentDetails;
    }

    public void setUseParentDetails(Boolean useParentDetails) {
        this.useParentDetails = useParentDetails;
    }

    public SaleStatus getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariationDTO)) {
            return false;
        }

        ProductVariationDTO productVariationDTO = (ProductVariationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productVariationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariationDTO{" +
            "id=" + getId() +
            ", assetId='" + getAssetId() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", regularPrice=" + getRegularPrice() +
            ", salePrice=" + getSalePrice() +
            ", dateOnSaleFrom='" + getDateOnSaleFrom() + "'" +
            ", dateOnSaleTo='" + getDateOnSaleTo() + "'" +
            ", isDraft='" + getIsDraft() + "'" +
            ", useParentDetails='" + getUseParentDetails() + "'" +
            ", saleStatus='" + getSaleStatus() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
