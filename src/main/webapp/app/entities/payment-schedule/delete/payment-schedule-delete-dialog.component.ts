import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentSchedule } from '../payment-schedule.model';
import { PaymentScheduleService } from '../service/payment-schedule.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './payment-schedule-delete-dialog.component.html',
})
export class PaymentScheduleDeleteDialogComponent {
  paymentSchedule?: IPaymentSchedule;

  constructor(protected paymentScheduleService: PaymentScheduleService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paymentScheduleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
