import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PhonenumberComponent } from '../list/phonenumber.component';
import { PhonenumberDetailComponent } from '../detail/phonenumber-detail.component';
import { PhonenumberUpdateComponent } from '../update/phonenumber-update.component';
import { PhonenumberRoutingResolveService } from './phonenumber-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const phonenumberRoute: Routes = [
  {
    path: '',
    component: PhonenumberComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PhonenumberDetailComponent,
    resolve: {
      phonenumber: PhonenumberRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PhonenumberUpdateComponent,
    resolve: {
      phonenumber: PhonenumberRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PhonenumberUpdateComponent,
    resolve: {
      phonenumber: PhonenumberRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(phonenumberRoute)],
  exports: [RouterModule],
})
export class PhonenumberRoutingModule {}
