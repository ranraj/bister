import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductVariationFormService, ProductVariationFormGroup } from './product-variation-form.service';
import { IProductVariation } from '../product-variation.model';
import { ProductVariationService } from '../service/product-variation.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { SaleStatus } from 'app/entities/enumerations/sale-status.model';

@Component({
  selector: 'yali-product-variation-update',
  templateUrl: './product-variation-update.component.html',
})
export class ProductVariationUpdateComponent implements OnInit {
  isSaving = false;
  productVariation: IProductVariation | null = null;
  saleStatusValues = Object.keys(SaleStatus);

  productsSharedCollection: IProduct[] = [];

  editForm: ProductVariationFormGroup = this.productVariationFormService.createProductVariationFormGroup();

  constructor(
    protected productVariationService: ProductVariationService,
    protected productVariationFormService: ProductVariationFormService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVariation }) => {
      this.productVariation = productVariation;
      if (productVariation) {
        this.updateForm(productVariation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productVariation = this.productVariationFormService.getProductVariation(this.editForm);
    if (productVariation.id !== null) {
      this.subscribeToSaveResponse(this.productVariationService.update(productVariation));
    } else {
      this.subscribeToSaveResponse(this.productVariationService.create(productVariation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductVariation>>): void {
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

  protected updateForm(productVariation: IProductVariation): void {
    this.productVariation = productVariation;
    this.productVariationFormService.resetForm(this.editForm, productVariation);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productVariation.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productVariation?.product)
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
