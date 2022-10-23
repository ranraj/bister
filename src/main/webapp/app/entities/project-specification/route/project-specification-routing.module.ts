import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjectSpecificationComponent } from '../list/project-specification.component';
import { ProjectSpecificationDetailComponent } from '../detail/project-specification-detail.component';
import { ProjectSpecificationUpdateComponent } from '../update/project-specification-update.component';
import { ProjectSpecificationRoutingResolveService } from './project-specification-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const projectSpecificationRoute: Routes = [
  {
    path: '',
    component: ProjectSpecificationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjectSpecificationDetailComponent,
    resolve: {
      projectSpecification: ProjectSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjectSpecificationUpdateComponent,
    resolve: {
      projectSpecification: ProjectSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjectSpecificationUpdateComponent,
    resolve: {
      projectSpecification: ProjectSpecificationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projectSpecificationRoute)],
  exports: [RouterModule],
})
export class ProjectSpecificationRoutingModule {}
