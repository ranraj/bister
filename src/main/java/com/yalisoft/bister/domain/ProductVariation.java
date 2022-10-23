package com.yalisoft.bister.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yalisoft.bister.domain.enumeration.SaleStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductVariation.
 */
@Table("product_variation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productvariation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("asset_id")
    private String assetId;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 100)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 1000)
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("regular_price")
    private BigDecimal regularPrice;

    @NotNull(message = "must not be null")
    @Column("sale_price")
    private BigDecimal salePrice;

    @NotNull(message = "must not be null")
    @Column("date_on_sale_from")
    private LocalDate dateOnSaleFrom;

    @NotNull(message = "must not be null")
    @Column("date_on_sale_to")
    private LocalDate dateOnSaleTo;

    @NotNull(message = "must not be null")
    @Column("is_draft")
    private Boolean isDraft;

    @NotNull(message = "must not be null")
    @Column("use_parent_details")
    private Boolean useParentDetails;

    @NotNull(message = "must not be null")
    @Column("sale_status")
    private SaleStatus saleStatus;

    @Transient
    @JsonIgnoreProperties(value = { "productVariation" }, allowSetters = true)
    private Set<ProductVariationAttributeTerm> productVariationAttributeTerms = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = {
            "productVariations",
            "productAttributeTerms",
            "tags",
            "productReviews",
            "productSpecifications",
            "attachments",
            "enquiries",
            "productActivities",
            "project",
            "categories",
            "taxClass",
        },
        allowSetters = true
    )
    private Product product;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductVariation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetId() {
        return this.assetId;
    }

    public ProductVariation assetId(String assetId) {
        this.setAssetId(assetId);
        return this;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return this.name;
    }

    public ProductVariation name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductVariation description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRegularPrice() {
        return this.regularPrice;
    }

    public ProductVariation regularPrice(BigDecimal regularPrice) {
        this.setRegularPrice(regularPrice);
        return this;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice != null ? regularPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public ProductVariation salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice != null ? salePrice.stripTrailingZeros() : null;
    }

    public LocalDate getDateOnSaleFrom() {
        return this.dateOnSaleFrom;
    }

    public ProductVariation dateOnSaleFrom(LocalDate dateOnSaleFrom) {
        this.setDateOnSaleFrom(dateOnSaleFrom);
        return this;
    }

    public void setDateOnSaleFrom(LocalDate dateOnSaleFrom) {
        this.dateOnSaleFrom = dateOnSaleFrom;
    }

    public LocalDate getDateOnSaleTo() {
        return this.dateOnSaleTo;
    }

    public ProductVariation dateOnSaleTo(LocalDate dateOnSaleTo) {
        this.setDateOnSaleTo(dateOnSaleTo);
        return this;
    }

    public void setDateOnSaleTo(LocalDate dateOnSaleTo) {
        this.dateOnSaleTo = dateOnSaleTo;
    }

    public Boolean getIsDraft() {
        return this.isDraft;
    }

    public ProductVariation isDraft(Boolean isDraft) {
        this.setIsDraft(isDraft);
        return this;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public Boolean getUseParentDetails() {
        return this.useParentDetails;
    }

    public ProductVariation useParentDetails(Boolean useParentDetails) {
        this.setUseParentDetails(useParentDetails);
        return this;
    }

    public void setUseParentDetails(Boolean useParentDetails) {
        this.useParentDetails = useParentDetails;
    }

    public SaleStatus getSaleStatus() {
        return this.saleStatus;
    }

    public ProductVariation saleStatus(SaleStatus saleStatus) {
        this.setSaleStatus(saleStatus);
        return this;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Set<ProductVariationAttributeTerm> getProductVariationAttributeTerms() {
        return this.productVariationAttributeTerms;
    }

    public void setProductVariationAttributeTerms(Set<ProductVariationAttributeTerm> productVariationAttributeTerms) {
        if (this.productVariationAttributeTerms != null) {
            this.productVariationAttributeTerms.forEach(i -> i.setProductVariation(null));
        }
        if (productVariationAttributeTerms != null) {
            productVariationAttributeTerms.forEach(i -> i.setProductVariation(this));
        }
        this.productVariationAttributeTerms = productVariationAttributeTerms;
    }

    public ProductVariation productVariationAttributeTerms(Set<ProductVariationAttributeTerm> productVariationAttributeTerms) {
        this.setProductVariationAttributeTerms(productVariationAttributeTerms);
        return this;
    }

    public ProductVariation addProductVariationAttributeTerm(ProductVariationAttributeTerm productVariationAttributeTerm) {
        this.productVariationAttributeTerms.add(productVariationAttributeTerm);
        productVariationAttributeTerm.setProductVariation(this);
        return this;
    }

    public ProductVariation removeProductVariationAttributeTerm(ProductVariationAttributeTerm productVariationAttributeTerm) {
        this.productVariationAttributeTerms.remove(productVariationAttributeTerm);
        productVariationAttributeTerm.setProductVariation(null);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductVariation product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariation)) {
            return false;
        }
        return id != null && id.equals(((ProductVariation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariation{" +
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
            "}";
    }
}
