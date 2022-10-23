import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjectReviewComponent } from '../list/project-review.component';
import { ProjectReviewDetailComponent } from '../detail/project-review-detail.component';
import { ProjectReviewUpdateComponent } from '../update/project-review-update.component';
import { ProjectReviewRoutingResolveService } from './project-review-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const projectReviewRoute: Routes = [
  {
    path: '',
    component: ProjectReviewComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjectReviewDetailComponent,
    resolve: {
      projectReview: ProjectReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjectReviewUpdateComponent,
    resolve: {
      projectReview: ProjectReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjectReviewUpdateComponent,
    resolve: {
      projectReview: ProjectReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projectReviewRoute)],
  exports: [RouterModule],
})
export class ProjectReviewRoutingModule {}
