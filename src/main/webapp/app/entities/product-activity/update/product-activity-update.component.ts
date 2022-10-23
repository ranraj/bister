import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductActivityFormService, ProductActivityFormGroup } from './product-activity-form.service';
import { IProductActivity } from '../product-activity.model';
import { ProductActivityService } from '../service/product-activity.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

@Component({
  selector: 'yali-product-activity-update',
  templateUrl: './product-activity-update.component.html',
})
export class ProductActivityUpdateComponent implements OnInit {
  isSaving = false;
  productActivity: IProductActivity | null = null;
  activityStatusValues = Object.keys(ActivityStatus);

  productsSharedCollection: IProduct[] = [];

  editForm: ProductActivityFormGroup = this.productActivityFormService.createProductActivityFormGroup();

  constructor(
    protected productActivityService: ProductActivityService,
    protected productActivityFormService: ProductActivityFormService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productActivity }) => {
      this.productActivity = productActivity;
      if (productActivity) {
        this.updateForm(productActivity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productActivity = this.productActivityFormService.getProductActivity(this.editForm);
    if (productActivity.id !== null) {
      this.subscribeToSaveResponse(this.productActivityService.update(productActivity));
    } else {
      this.subscribeToSaveResponse(this.productActivityService.create(productActivity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductActivity>>): void {
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

  protected updateForm(productActivity: IProductActivity): void {
    this.productActivity = productActivity;
    this.productActivityFormService.resetForm(this.editForm, productActivity);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productActivity.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productActivity?.product)
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
