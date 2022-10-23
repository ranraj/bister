import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductActivityComponent } from '../list/product-activity.component';
import { ProductActivityDetailComponent } from '../detail/product-activity-detail.component';
import { ProductActivityUpdateComponent } from '../update/product-activity-update.component';
import { ProductActivityRoutingResolveService } from './product-activity-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productActivityRoute: Routes = [
  {
    path: '',
    component: ProductActivityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductActivityDetailComponent,
    resolve: {
      productActivity: ProductActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductActivityUpdateComponent,
    resolve: {
      productActivity: ProductActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductActivityUpdateComponent,
    resolve: {
      productActivity: ProductActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productActivityRoute)],
  exports: [RouterModule],
})
export class ProductActivityRoutingModule {}
