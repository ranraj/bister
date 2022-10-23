import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBookingOrder } from '../booking-order.model';
import { BookingOrderService } from '../service/booking-order.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './booking-order-delete-dialog.component.html',
})
export class BookingOrderDeleteDialogComponent {
  bookingOrder?: IBookingOrder;

  constructor(protected bookingOrderService: BookingOrderService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookingOrderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
