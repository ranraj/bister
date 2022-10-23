import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BookingOrderComponent } from './list/booking-order.component';
import { BookingOrderDetailComponent } from './detail/booking-order-detail.component';
import { BookingOrderUpdateComponent } from './update/booking-order-update.component';
import { BookingOrderDeleteDialogComponent } from './delete/booking-order-delete-dialog.component';
import { BookingOrderRoutingModule } from './route/booking-order-routing.module';

@NgModule({
  imports: [SharedModule, BookingOrderRoutingModule],
  declarations: [BookingOrderComponent, BookingOrderDetailComponent, BookingOrderUpdateComponent, BookingOrderDeleteDialogComponent],
})
export class BookingOrderModule {}
