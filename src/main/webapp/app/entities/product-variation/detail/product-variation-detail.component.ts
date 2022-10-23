import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductVariation } from '../product-variation.model';

@Component({
  selector: 'yali-product-variation-detail',
  templateUrl: './product-variation-detail.component.html',
})
export class ProductVariationDetailComponent implements OnInit {
  productVariation: IProductVariation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVariation }) => {
      this.productVariation = productVariation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
