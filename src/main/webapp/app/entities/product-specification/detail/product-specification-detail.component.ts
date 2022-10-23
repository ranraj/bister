import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductSpecification } from '../product-specification.model';

@Component({
  selector: 'yali-product-specification-detail',
  templateUrl: './product-specification-detail.component.html',
})
export class ProductSpecificationDetailComponent implements OnInit {
  productSpecification: IProductSpecification | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productSpecification }) => {
      this.productSpecification = productSpecification;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
