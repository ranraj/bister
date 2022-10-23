import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PromotionComponent } from '../list/promotion.component';
import { PromotionDetailComponent } from '../detail/promotion-detail.component';
import { PromotionUpdateComponent } from '../update/promotion-update.component';
import { PromotionRoutingResolveService } from './promotion-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const promotionRoute: Routes = [
  {
    path: '',
    component: PromotionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PromotionDetailComponent,
    resolve: {
      promotion: PromotionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PromotionUpdateComponent,
    resolve: {
      promotion: PromotionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PromotionUpdateComponent,
    resolve: {
      promotion: PromotionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(promotionRoute)],
  exports: [RouterModule],
})
export class PromotionRoutingModule {}
