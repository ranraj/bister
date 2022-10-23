import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjectTypeComponent } from '../list/project-type.component';
import { ProjectTypeDetailComponent } from '../detail/project-type-detail.component';
import { ProjectTypeUpdateComponent } from '../update/project-type-update.component';
import { ProjectTypeRoutingResolveService } from './project-type-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const projectTypeRoute: Routes = [
  {
    path: '',
    component: ProjectTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjectTypeDetailComponent,
    resolve: {
      projectType: ProjectTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjectTypeUpdateComponent,
    resolve: {
      projectType: ProjectTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjectTypeUpdateComponent,
    resolve: {
      projectType: ProjectTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projectTypeRoute)],
  exports: [RouterModule],
})
export class ProjectTypeRoutingModule {}
