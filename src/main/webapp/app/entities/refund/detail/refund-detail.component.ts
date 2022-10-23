import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRefund } from '../refund.model';

@Component({
  selector: 'yali-refund-detail',
  templateUrl: './refund-detail.component.html',
})
export class RefundDetailComponent implements OnInit {
  refund: IRefund | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refund }) => {
      this.refund = refund;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
