import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductSpecificationGroupFormService, ProductSpecificationGroupFormGroup } from './product-specification-group-form.service';
import { IProductSpecificationGroup } from '../product-specification-group.model';
import { ProductSpecificationGroupService } from '../service/product-specification-group.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'yali-product-specification-group-update',
  templateUrl: './product-specification-group-update.component.html',
})
export class ProductSpecificationGroupUpdateComponent implements OnInit {
  isSaving = false;
  productSpecificationGroup: IProductSpecificationGroup | null = null;

  productsSharedCollection: IProduct[] = [];

  editForm: ProductSpecificationGroupFormGroup = this.productSpecificationGroupFormService.createProductSpecificationGroupFormGroup();

  constructor(
    protected productSpecificationGroupService: ProductSpecificationGroupService,
    protected productSpecificationGroupFormService: ProductSpecificationGroupFormService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productSpecificationGroup }) => {
      this.productSpecificationGroup = productSpecificationGroup;
      if (productSpecificationGroup) {
        this.updateForm(productSpecificationGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productSpecificationGroup = this.productSpecificationGroupFormService.getProductSpecificationGroup(this.editForm);
    if (productSpecificationGroup.id !== null) {
      this.subscribeToSaveResponse(this.productSpecificationGroupService.update(productSpecificationGroup));
    } else {
      this.subscribeToSaveResponse(this.productSpecificationGroupService.create(productSpecificationGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductSpecificationGroup>>): void {
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

  protected updateForm(productSpecificationGroup: IProductSpecificationGroup): void {
    this.productSpecificationGroup = productSpecificationGroup;
    this.productSpecificationGroupFormService.resetForm(this.editForm, productSpecificationGroup);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productSpecificationGroup.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productSpecificationGroup?.product)
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
