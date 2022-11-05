import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductReviewComponent } from '../list/product-review.component';
import { ProductReviewDetailComponent } from '../detail/product-review-detail.component';
import { ProductReviewUpdateComponent } from '../update/product-review-update.component';
import { ProductReviewRoutingResolveService } from './product-review-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productReviewRoute: Routes = [
  {
    path: '',
    component: ProductReviewComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductReviewDetailComponent,
    resolve: {
      productReview: ProductReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductReviewUpdateComponent,
    resolve: {
      productReview: ProductReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductReviewUpdateComponent,
    resolve: {
      productReview: ProductReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productReviewRoute)],
  exports: [RouterModule],
})
export class ProductReviewRoutingModule {}
