import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductAttributeTerm } from '../product-attribute-term.model';

@Component({
  selector: 'yali-product-attribute-term-detail',
  templateUrl: './product-attribute-term-detail.component.html',
})
export class ProductAttributeTermDetailComponent implements OnInit {
  productAttributeTerm: IProductAttributeTerm | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productAttributeTerm }) => {
      this.productAttributeTerm = productAttributeTerm;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
