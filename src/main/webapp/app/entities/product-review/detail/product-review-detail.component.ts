import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductReview } from '../product-review.model';

@Component({
  selector: 'yali-product-review-detail',
  templateUrl: './product-review-detail.component.html',
})
export class ProductReviewDetailComponent implements OnInit {
  productReview: IProductReview | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productReview }) => {
      this.productReview = productReview;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
