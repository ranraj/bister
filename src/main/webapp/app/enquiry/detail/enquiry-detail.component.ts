import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEnquiry } from '../enquiry.model';

@Component({
  selector: 'yali-enquiry-detail',
  templateUrl: './enquiry-detail.component.html',
})
export class EnquiryDetailComponent implements OnInit {
  enquiry: IEnquiry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enquiry }) => {
      this.enquiry = enquiry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
