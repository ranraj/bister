import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductActivity } from '../product-activity.model';

@Component({
  selector: 'yali-product-activity-detail',
  templateUrl: './product-activity-detail.component.html',
})
export class ProductActivityDetailComponent implements OnInit {
  productActivity: IProductActivity | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productActivity }) => {
      this.productActivity = productActivity;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
