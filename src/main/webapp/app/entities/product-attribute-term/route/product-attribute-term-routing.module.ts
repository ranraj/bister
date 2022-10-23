import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductAttributeTermComponent } from '../list/product-attribute-term.component';
import { ProductAttributeTermDetailComponent } from '../detail/product-attribute-term-detail.component';
import { ProductAttributeTermUpdateComponent } from '../update/product-attribute-term-update.component';
import { ProductAttributeTermRoutingResolveService } from './product-attribute-term-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productAttributeTermRoute: Routes = [
  {
    path: '',
    component: ProductAttributeTermComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductAttributeTermDetailComponent,
    resolve: {
      productAttributeTerm: ProductAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductAttributeTermUpdateComponent,
    resolve: {
      productAttributeTerm: ProductAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductAttributeTermUpdateComponent,
    resolve: {
      productAttributeTerm: ProductAttributeTermRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productAttributeTermRoute)],
  exports: [RouterModule],
})
export class ProductAttributeTermRoutingModule {}
