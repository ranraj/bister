import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductAttributeTermFormService, ProductAttributeTermFormGroup } from './product-attribute-term-form.service';
import { IProductAttributeTerm } from '../product-attribute-term.model';
import { ProductAttributeTermService } from '../service/product-attribute-term.service';
import { IProductAttribute } from 'app/entities/product-attribute/product-attribute.model';
import { ProductAttributeService } from 'app/entities/product-attribute/service/product-attribute.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'yali-product-attribute-term-update',
  templateUrl: './product-attribute-term-update.component.html',
})
export class ProductAttributeTermUpdateComponent implements OnInit {
  isSaving = false;
  productAttributeTerm: IProductAttributeTerm | null = null;

  productAttributesSharedCollection: IProductAttribute[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm: ProductAttributeTermFormGroup = this.productAttributeTermFormService.createProductAttributeTermFormGroup();

  constructor(
    protected productAttributeTermService: ProductAttributeTermService,
    protected productAttributeTermFormService: ProductAttributeTermFormService,
    protected productAttributeService: ProductAttributeService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProductAttribute = (o1: IProductAttribute | null, o2: IProductAttribute | null): boolean =>
    this.productAttributeService.compareProductAttribute(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productAttributeTerm }) => {
      this.productAttributeTerm = productAttributeTerm;
      if (productAttributeTerm) {
        this.updateForm(productAttributeTerm);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productAttributeTerm = this.productAttributeTermFormService.getProductAttributeTerm(this.editForm);
    if (productAttributeTerm.id !== null) {
      this.subscribeToSaveResponse(this.productAttributeTermService.update(productAttributeTerm));
    } else {
      this.subscribeToSaveResponse(this.productAttributeTermService.create(productAttributeTerm));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductAttributeTerm>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(productAttributeTerm: IProductAttributeTerm): void {
    this.productAttributeTerm = productAttributeTerm;
    this.productAttributeTermFormService.resetForm(this.editForm, productAttributeTerm);

    this.productAttributesSharedCollection = this.productAttributeService.addProductAttributeToCollectionIfMissing<IProductAttribute>(
      this.productAttributesSharedCollection,
      productAttributeTerm.productAttribute
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productAttributeTerm.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productAttributeService
      .query()
      .pipe(map((res: HttpResponse<IProductAttribute[]>) => res.body ?? []))
      .pipe(
        map((productAttributes: IProductAttribute[]) =>
          this.productAttributeService.addProductAttributeToCollectionIfMissing<IProductAttribute>(
            productAttributes,
            this.productAttributeTerm?.productAttribute
          )
        )
      )
      .subscribe((productAttributes: IProductAttribute[]) => (this.productAttributesSharedCollection = productAttributes));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productAttributeTerm?.product)
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
