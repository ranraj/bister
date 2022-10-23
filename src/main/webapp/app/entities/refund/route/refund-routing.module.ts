import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RefundComponent } from '../list/refund.component';
import { RefundDetailComponent } from '../detail/refund-detail.component';
import { RefundUpdateComponent } from '../update/refund-update.component';
import { RefundRoutingResolveService } from './refund-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const refundRoute: Routes = [
  {
    path: '',
    component: RefundComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RefundDetailComponent,
    resolve: {
      refund: RefundRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RefundUpdateComponent,
    resolve: {
      refund: RefundRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RefundUpdateComponent,
    resolve: {
      refund: RefundRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(refundRoute)],
  exports: [RouterModule],
})
export class RefundRoutingModule {}
