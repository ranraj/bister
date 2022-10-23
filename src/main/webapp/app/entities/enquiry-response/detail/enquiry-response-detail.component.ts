import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEnquiryResponse } from '../enquiry-response.model';

@Component({
  selector: 'yali-enquiry-response-detail',
  templateUrl: './enquiry-response-detail.component.html',
})
export class EnquiryResponseDetailComponent implements OnInit {
  enquiryResponse: IEnquiryResponse | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enquiryResponse }) => {
      this.enquiryResponse = enquiryResponse;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
