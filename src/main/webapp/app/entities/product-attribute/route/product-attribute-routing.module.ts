import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductAttributeComponent } from '../list/product-attribute.component';
import { ProductAttributeDetailComponent } from '../detail/product-attribute-detail.component';
import { ProductAttributeUpdateComponent } from '../update/product-attribute-update.component';
import { ProductAttributeRoutingResolveService } from './product-attribute-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productAttributeRoute: Routes = [
  {
    path: '',
    component: ProductAttributeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductAttributeDetailComponent,
    resolve: {
      productAttribute: ProductAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductAttributeUpdateComponent,
    resolve: {
      productAttribute: ProductAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductAttributeUpdateComponent,
    resolve: {
      productAttribute: ProductAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productAttributeRoute)],
  exports: [RouterModule],
})
export class ProductAttributeRoutingModule {}
