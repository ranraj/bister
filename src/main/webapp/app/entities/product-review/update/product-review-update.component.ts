import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductReviewFormService, ProductReviewFormGroup } from './product-review-form.service';
import { IProductReview } from '../product-review.model';
import { ProductReviewService } from '../service/product-review.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

@Component({
  selector: 'yali-product-review-update',
  templateUrl: './product-review-update.component.html',
})
export class ProductReviewUpdateComponent implements OnInit {
  isSaving = false;
  productReview: IProductReview | null = null;
  reviewStatusValues = Object.keys(ReviewStatus);

  productsSharedCollection: IProduct[] = [];

  editForm: ProductReviewFormGroup = this.productReviewFormService.createProductReviewFormGroup();

  constructor(
    protected productReviewService: ProductReviewService,
    protected productReviewFormService: ProductReviewFormService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productReview }) => {
      this.productReview = productReview;
      if (productReview) {
        this.updateForm(productReview);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productReview = this.productReviewFormService.getProductReview(this.editForm);
    if (productReview.id !== null) {
      this.subscribeToSaveResponse(this.productReviewService.update(productReview));
    } else {
      this.subscribeToSaveResponse(this.productReviewService.create(productReview));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductReview>>): void {
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

  protected updateForm(productReview: IProductReview): void {
    this.productReview = productReview;
    this.productReviewFormService.resetForm(this.editForm, productReview);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      productReview.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.productReview?.product))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}
