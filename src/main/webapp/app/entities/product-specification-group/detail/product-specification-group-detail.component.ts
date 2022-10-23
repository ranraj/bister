import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductSpecificationGroup } from '../product-specification-group.model';

@Component({
  selector: 'yali-product-specification-group-detail',
  templateUrl: './product-specification-group-detail.component.html',
})
export class ProductSpecificationGroupDetailComponent implements OnInit {
  productSpecificationGroup: IProductSpecificationGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productSpecificationGroup }) => {
      this.productSpecificationGroup = productSpecificationGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
