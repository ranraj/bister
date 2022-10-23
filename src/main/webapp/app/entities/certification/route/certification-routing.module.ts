import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CertificationComponent } from '../list/certification.component';
import { CertificationDetailComponent } from '../detail/certification-detail.component';
import { CertificationUpdateComponent } from '../update/certification-update.component';
import { CertificationRoutingResolveService } from './certification-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const certificationRoute: Routes = [
  {
    path: '',
    component: CertificationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CertificationDetailComponent,
    resolve: {
      certification: CertificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CertificationUpdateComponent,
    resolve: {
      certification: CertificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CertificationUpdateComponent,
    resolve: {
      certification: CertificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(certificationRoute)],
  exports: [RouterModule],
})
export class CertificationRoutingModule {}
