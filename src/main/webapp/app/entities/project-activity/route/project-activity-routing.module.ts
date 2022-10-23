import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjectActivityComponent } from '../list/project-activity.component';
import { ProjectActivityDetailComponent } from '../detail/project-activity-detail.component';
import { ProjectActivityUpdateComponent } from '../update/project-activity-update.component';
import { ProjectActivityRoutingResolveService } from './project-activity-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const projectActivityRoute: Routes = [
  {
    path: '',
    component: ProjectActivityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjectActivityDetailComponent,
    resolve: {
      projectActivity: ProjectActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjectActivityUpdateComponent,
    resolve: {
      projectActivity: ProjectActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjectActivityUpdateComponent,
    resolve: {
      projectActivity: ProjectActivityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projectActivityRoute)],
  exports: [RouterModule],
})
export class ProjectActivityRoutingModule {}
