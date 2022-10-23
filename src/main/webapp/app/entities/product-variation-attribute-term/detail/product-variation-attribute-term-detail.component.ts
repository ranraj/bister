import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';

@Component({
  selector: 'yali-product-variation-attribute-term-detail',
  templateUrl: './product-variation-attribute-term-detail.component.html',
})
export class ProductVariationAttributeTermDetailComponent implements OnInit {
  productVariationAttributeTerm: IProductVariationAttributeTerm | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVariationAttributeTerm }) => {
      this.productVariationAttributeTerm = productVariationAttributeTerm;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
