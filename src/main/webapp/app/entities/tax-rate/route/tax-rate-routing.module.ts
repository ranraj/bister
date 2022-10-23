import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TaxRateComponent } from '../list/tax-rate.component';
import { TaxRateDetailComponent } from '../detail/tax-rate-detail.component';
import { TaxRateUpdateComponent } from '../update/tax-rate-update.component';
import { TaxRateRoutingResolveService } from './tax-rate-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const taxRateRoute: Routes = [
  {
    path: '',
    component: TaxRateComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TaxRateDetailComponent,
    resolve: {
      taxRate: TaxRateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TaxRateUpdateComponent,
    resolve: {
      taxRate: TaxRateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TaxRateUpdateComponent,
    resolve: {
      taxRate: TaxRateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(taxRateRoute)],
  exports: [RouterModule],
})
export class TaxRateRoutingModule {}
