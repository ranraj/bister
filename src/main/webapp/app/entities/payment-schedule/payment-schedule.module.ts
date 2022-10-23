import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaymentScheduleComponent } from './list/payment-schedule.component';
import { PaymentScheduleDetailComponent } from './detail/payment-schedule-detail.component';
import { PaymentScheduleUpdateComponent } from './update/payment-schedule-update.component';
import { PaymentScheduleDeleteDialogComponent } from './delete/payment-schedule-delete-dialog.component';
import { PaymentScheduleRoutingModule } from './route/payment-schedule-routing.module';

@NgModule({
  imports: [SharedModule, PaymentScheduleRoutingModule],
  declarations: [
    PaymentScheduleComponent,
    PaymentScheduleDetailComponent,
    PaymentScheduleUpdateComponent,
    PaymentScheduleDeleteDialogComponent,
  ],
})
export class PaymentScheduleModule {}
