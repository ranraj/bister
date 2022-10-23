import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductAttribute } from '../product-attribute.model';

@Component({
  selector: 'yali-product-attribute-detail',
  templateUrl: './product-attribute-detail.component.html',
})
export class ProductAttributeDetailComponent implements OnInit {
  productAttribute: IProductAttribute | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productAttribute }) => {
      this.productAttribute = productAttribute;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
