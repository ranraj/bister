import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BookingOrderComponent } from '../list/booking-order.component';
import { BookingOrderDetailComponent } from '../detail/booking-order-detail.component';
import { BookingOrderUpdateComponent } from '../update/booking-order-update.component';
import { BookingOrderRoutingResolveService } from './booking-order-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const bookingOrderRoute: Routes = [
  {
    path: '',
    component: BookingOrderComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookingOrderDetailComponent,
    resolve: {
      bookingOrder: BookingOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookingOrderUpdateComponent,
    resolve: {
      bookingOrder: BookingOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookingOrderUpdateComponent,
    resolve: {
      bookingOrder: BookingOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bookingOrderRoute)],
  exports: [RouterModule],
})
export class BookingOrderRoutingModule {}
