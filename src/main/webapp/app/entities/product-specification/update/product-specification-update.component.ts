import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductSpecificationFormService, ProductSpecificationFormGroup } from './product-specification-form.service';
import { IProductSpecification } from '../product-specification.model';
import { ProductSpecificationService } from '../service/product-specification.service';
import { IProductSpecificationGroup } from 'app/entities/product-specification-group/product-specification-group.model';
import { ProductSpecificationGroupService } from 'app/entities/product-specification-group/service/product-specification-group.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'yali-product-specification-update',
  templateUrl: './product-specification-update.component.html',
})
export class ProductSpecificationUpdateComponent implements OnInit {
  isSaving = false;
  productSpecification: IProductSpecification | null = null;

  productSpecificationGroupsSharedCollection: IProductSpecificationGroup[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm: ProductSpecificationFormGroup = this.productSpecificationFormService.createProductSpecificationFormGroup();

  constructor(
    protected productSpecificationService: ProductSpecificationService,
    protected productSpecificationFormService: ProductSpecificationFormService,
    protected productSpecificationGroupService: ProductSpecificationGroupService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProductSpecificationGroup = (o1: IProductSpecificationGroup | null, o2: IProductSpecificationGroup | null): boolean =>
    this.productSpecificationGroupService.compareProductSpecificationGroup(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productSpecification }) => {
      this.productSpecification = productSpecification;
      if (productSpecification) {
        this.updateForm(productSpecification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productSpecification = this.productSpecificationFormService.getProductSpecification(this.editForm);
    if (productSpecification.id !== null) {
      this.subscribeToSaveResponse(this.productSpecificationService.update(productSpecification));
    } else {
      this.subscribeToSaveResponse(this.productSpecificationService.create(productSpecification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductSpecification>>): void {
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

  protected updateForm(productSpecification: IProductSpecification): void {
    this.productSpecification = productSpecification;
    this.productSpecificationFormService.resetForm(this.editForm, productSpecification);

    this.productSpecificationGroupsSharedCollection =
      this.productSpecificationGroupService.addProductSpecificationGroupToCollectionIfMissing<IProductSpecificationGroup>(
        this.productSpecificationGroupsSharedCollection,
        productSpecification.productSpecificationGroup
      );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productSpecification.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productSpecificationGroupService
      .query()
      .pipe(map((res: HttpResponse<IProductSpecificationGroup[]>) => res.body ?? []))
      .pipe(
        map((productSpecificationGroups: IProductSpecificationGroup[]) =>
          this.productSpecificationGroupService.addProductSpecificationGroupToCollectionIfMissing<IProductSpecificationGroup>(
            productSpecificationGroups,
            this.productSpecification?.productSpecificationGroup
          )
        )
      )
      .subscribe(
        (productSpecificationGroups: IProductSpecificationGroup[]) =>
          (this.productSpecificationGroupsSharedCollection = productSpecificationGroups)
      );

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productSpecification?.product)
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
