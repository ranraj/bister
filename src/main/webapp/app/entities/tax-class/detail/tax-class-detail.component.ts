import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITaxClass } from '../tax-class.model';

@Component({
  selector: 'yali-tax-class-detail',
  templateUrl: './tax-class-detail.component.html',
})
export class TaxClassDetailComponent implements OnInit {
  taxClass: ITaxClass | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taxClass }) => {
      this.taxClass = taxClass;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
