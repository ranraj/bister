import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductSpecificationGroupComponent } from '../list/product-specification-group.component';
import { ProductSpecificationGroupDetailComponent } from '../detail/product-specification-group-detail.component';
import { ProductSpecificationGroupUpdateComponent } from '../update/product-specification-group-update.component';
import { ProductSpecificationGroupRoutingResolveService } from './product-specification-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productSpecificationGroupRoute: Routes = [
  {
    path: '',
    component: ProductSpecificationGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductSpecificationGroupDetailComponent,
    resolve: {
      productSpecificationGroup: ProductSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductSpecificationGroupUpdateComponent,
    resolve: {
      productSpecificationGroup: ProductSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductSpecificationGroupUpdateComponent,
    resolve: {
      productSpecificationGroup: ProductSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productSpecificationGroupRoute)],
  exports: [RouterModule],
})
export class ProductSpecificationGroupRoutingModule {}
