import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBusinessPartner } from '../business-partner.model';

@Component({
  selector: 'yali-business-partner-detail',
  templateUrl: './business-partner-detail.component.html',
})
export class BusinessPartnerDetailComponent implements OnInit {
  businessPartner: IBusinessPartner | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businessPartner }) => {
      this.businessPartner = businessPartner;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
