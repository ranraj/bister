import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FacilityComponent } from '../list/facility.component';
import { FacilityDetailComponent } from '../detail/facility-detail.component';
import { FacilityUpdateComponent } from '../update/facility-update.component';
import { FacilityRoutingResolveService } from './facility-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const facilityRoute: Routes = [
  {
    path: '',
    component: FacilityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FacilityDetailComponent,
    resolve: {
      facility: FacilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FacilityUpdateComponent,
    resolve: {
      facility: FacilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FacilityUpdateComponent,
    resolve: {
      facility: FacilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(facilityRoute)],
  exports: [RouterModule],
})
export class FacilityRoutingModule {}
