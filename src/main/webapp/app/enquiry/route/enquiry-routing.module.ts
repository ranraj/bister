import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnquiryComponent } from '../list/enquiry.component';
import { EnquiryDetailComponent } from '../detail/enquiry-detail.component';
import { EnquiryUpdateComponent } from '../update/enquiry-update.component';
import { EnquiryRoutingResolveService } from './enquiry-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const enquiryRoute: Routes = [
  {
    path: '',
    component: EnquiryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnquiryDetailComponent,
    resolve: {
      enquiry: EnquiryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnquiryUpdateComponent,
    resolve: {
      enquiry: EnquiryRoutingResolveService,
    },
  },
  {
    path: ':id/edit',
    component: EnquiryUpdateComponent,
    resolve: {
      enquiry: EnquiryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(enquiryRoute)],
  exports: [RouterModule],
})
export class EnquiryRoutingModule {}
