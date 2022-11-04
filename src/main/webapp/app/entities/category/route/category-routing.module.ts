import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CategoryComponent } from '../list/category.component';
import { CategoryDetailComponent } from '../detail/category-detail.component';
import { CategoryUpdateComponent } from '../update/category-update.component';
import { CategoryRoutingResolveService } from './category-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const categoryRoute: Routes = [
  {
    path: '',
    component: CategoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
  },
  {
    path: ':id/view',
    component: CategoryDetailComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
  },
  {
    path: 'new',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(categoryRoute)],
  exports: [RouterModule],
})
export class CategoryRoutingModule {}
