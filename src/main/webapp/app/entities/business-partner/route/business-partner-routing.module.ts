import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BusinessPartnerComponent } from '../list/business-partner.component';
import { BusinessPartnerDetailComponent } from '../detail/business-partner-detail.component';
import { BusinessPartnerUpdateComponent } from '../update/business-partner-update.component';
import { BusinessPartnerRoutingResolveService } from './business-partner-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const businessPartnerRoute: Routes = [
  {
    path: '',
    component: BusinessPartnerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BusinessPartnerDetailComponent,
    resolve: {
      businessPartner: BusinessPartnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BusinessPartnerUpdateComponent,
    resolve: {
      businessPartner: BusinessPartnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BusinessPartnerUpdateComponent,
    resolve: {
      businessPartner: BusinessPartnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(businessPartnerRoute)],
  exports: [RouterModule],
})
export class BusinessPartnerRoutingModule {}
