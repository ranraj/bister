import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductSpecificationComponent } from '../list/product-specification.component';
import { ProductSpecificationDetailComponent } from '../detail/product-specification-detail.component';
import { ProductSpecificationUpdateComponent } from '../update/product-specification-update.component';
import { ProductSpecificationRoutingResolveService } from './product-specification-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productSpecificationRoute: Routes = [
  {
    path: '',
    component: ProductSpecificationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductSpecificationDetailComponent,
    resolve: {
      productSpecification: ProductSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductSpecificationUpdateComponent,
    resolve: {
      productSpecification: ProductSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductSpecificationUpdateComponent,
    resolve: {
      productSpecification: ProductSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productSpecificationRoute)],
  exports: [RouterModule],
})
export class ProductSpecificationRoutingModule {}
