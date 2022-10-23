import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjectSpecificationGroupComponent } from '../list/project-specification-group.component';
import { ProjectSpecificationGroupDetailComponent } from '../detail/project-specification-group-detail.component';
import { ProjectSpecificationGroupUpdateComponent } from '../update/project-specification-group-update.component';
import { ProjectSpecificationGroupRoutingResolveService } from './project-specification-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const projectSpecificationGroupRoute: Routes = [
  {
    path: '',
    component: ProjectSpecificationGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjectSpecificationGroupDetailComponent,
    resolve: {
      projectSpecificationGroup: ProjectSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjectSpecificationGroupUpdateComponent,
    resolve: {
      projectSpecificationGroup: ProjectSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjectSpecificationGroupUpdateComponent,
    resolve: {
      projectSpecificationGroup: ProjectSpecificationGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projectSpecificationGroupRoute)],
  exports: [RouterModule],
})
export class ProjectSpecificationGroupRoutingModule {}
