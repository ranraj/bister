import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaymentScheduleComponent } from '../list/payment-schedule.component';
import { PaymentScheduleDetailComponent } from '../detail/payment-schedule-detail.component';
import { PaymentScheduleUpdateComponent } from '../update/payment-schedule-update.component';
import { PaymentScheduleRoutingResolveService } from './payment-schedule-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const paymentScheduleRoute: Routes = [
  {
    path: '',
    component: PaymentScheduleComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaymentScheduleDetailComponent,
    resolve: {
      paymentSchedule: PaymentScheduleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaymentScheduleUpdateComponent,
    resolve: {
      paymentSchedule: PaymentScheduleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaymentScheduleUpdateComponent,
    resolve: {
      paymentSchedule: PaymentScheduleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paymentScheduleRoute)],
  exports: [RouterModule],
})
export class PaymentScheduleRoutingModule {}
