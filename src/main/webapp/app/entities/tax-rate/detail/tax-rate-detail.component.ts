import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITaxRate } from '../tax-rate.model';

@Component({
  selector: 'yali-tax-rate-detail',
  templateUrl: './tax-rate-detail.component.html',
})
export class TaxRateDetailComponent implements OnInit {
  taxRate: ITaxRate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taxRate }) => {
      this.taxRate = taxRate;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
