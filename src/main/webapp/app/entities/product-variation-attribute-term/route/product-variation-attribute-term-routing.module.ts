import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductVariationAttributeTermComponent } from '../list/product-variation-attribute-term.component';
import { ProductVariationAttributeTermDetailComponent } from '../detail/product-variation-attribute-term-detail.component';
import { ProductVariationAttributeTermUpdateComponent } from '../update/product-variation-attribute-term-update.component';
import { ProductVariationAttributeTermRoutingResolveService } from './product-variation-attribute-term-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productVariationAttributeTermRoute: Routes = [
  {
    path: '',
    component: ProductVariationAttributeTermComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductVariationAttributeTermDetailComponent,
    resolve: {
      productVariationAttributeTerm: ProductVariationAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductVariationAttributeTermUpdateComponent,
    resolve: {
      productVariationAttributeTerm: ProductVariationAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductVariationAttributeTermUpdateComponent,
    resolve: {
      productVariationAttributeTerm: ProductVariationAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productVariationAttributeTermRoute)],
  exports: [RouterModule],
})
export class ProductVariationAttributeTermRoutingModule {}
