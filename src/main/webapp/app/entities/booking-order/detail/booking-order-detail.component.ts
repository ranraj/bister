import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBookingOrder } from '../booking-order.model';

@Component({
  selector: 'yali-booking-order-detail',
  templateUrl: './booking-order-detail.component.html',
})
export class BookingOrderDetailComponent implements OnInit {
  bookingOrder: IBookingOrder | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookingOrder }) => {
      this.bookingOrder = bookingOrder;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
