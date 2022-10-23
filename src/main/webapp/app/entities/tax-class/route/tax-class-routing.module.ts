import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TaxClassComponent } from '../list/tax-class.component';
import { TaxClassDetailComponent } from '../detail/tax-class-detail.component';
import { TaxClassUpdateComponent } from '../update/tax-class-update.component';
import { TaxClassRoutingResolveService } from './tax-class-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const taxClassRoute: Routes = [
  {
    path: '',
    component: TaxClassComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TaxClassDetailComponent,
    resolve: {
      taxClass: TaxClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TaxClassUpdateComponent,
    resolve: {
      taxClass: TaxClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TaxClassUpdateComponent,
    resolve: {
      taxClass: TaxClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(taxClassRoute)],
  exports: [RouterModule],
})
export class TaxClassRoutingModule {}
