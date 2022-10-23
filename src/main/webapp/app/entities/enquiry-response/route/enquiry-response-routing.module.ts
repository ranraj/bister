import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnquiryResponseComponent } from '../list/enquiry-response.component';
import { EnquiryResponseDetailComponent } from '../detail/enquiry-response-detail.component';
import { EnquiryResponseUpdateComponent } from '../update/enquiry-response-update.component';
import { EnquiryResponseRoutingResolveService } from './enquiry-response-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const enquiryResponseRoute: Routes = [
  {
    path: '',
    component: EnquiryResponseComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnquiryResponseDetailComponent,
    resolve: {
      enquiryResponse: EnquiryResponseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnquiryResponseUpdateComponent,
    resolve: {
      enquiryResponse: EnquiryResponseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnquiryResponseUpdateComponent,
    resolve: {
      enquiryResponse: EnquiryResponseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(enquiryResponseRoute)],
  exports: [RouterModule],
})
export class EnquiryResponseRoutingModule {}
