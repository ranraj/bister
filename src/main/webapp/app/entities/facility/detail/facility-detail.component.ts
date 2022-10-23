import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFacility } from '../facility.model';

@Component({
  selector: 'yali-facility-detail',
  templateUrl: './facility-detail.component.html',
})
export class FacilityDetailComponent implements OnInit {
  facility: IFacility | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ facility }) => {
      this.facility = facility;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
