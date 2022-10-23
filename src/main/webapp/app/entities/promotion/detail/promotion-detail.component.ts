import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPromotion } from '../promotion.model';

@Component({
  selector: 'yali-promotion-detail',
  templateUrl: './promotion-detail.component.html',
})
export class PromotionDetailComponent implements OnInit {
  promotion: IPromotion | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ promotion }) => {
      this.promotion = promotion;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
