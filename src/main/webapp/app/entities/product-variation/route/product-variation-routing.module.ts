import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductVariationComponent } from '../list/product-variation.component';
import { ProductVariationDetailComponent } from '../detail/product-variation-detail.component';
import { ProductVariationUpdateComponent } from '../update/product-variation-update.component';
import { ProductVariationRoutingResolveService } from './product-variation-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productVariationRoute: Routes = [
  {
    path: '',
    component: ProductVariationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductVariationDetailComponent,
    resolve: {
      productVariation: ProductVariationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductVariationUpdateComponent,
    resolve: {
      productVariation: ProductVariationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductVariationUpdateComponent,
    resolve: {
      productVariation: ProductVariationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productVariationRoute)],
  exports: [RouterModule],
})
export class ProductVariationRoutingModule {}
