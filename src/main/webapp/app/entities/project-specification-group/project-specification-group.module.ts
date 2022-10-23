import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjectSpecificationGroupComponent } from './list/project-specification-group.component';
import { ProjectSpecificationGroupDetailComponent } from './detail/project-specification-group-detail.component';
import { ProjectSpecificationGroupUpdateComponent } from './update/project-specification-group-update.component';
import { ProjectSpecificationGroupDeleteDialogComponent } from './delete/project-specification-group-delete-dialog.component';
import { ProjectSpecificationGroupRoutingModule } from './route/project-specification-group-routing.module';

@NgModule({
  imports: [SharedModule, ProjectSpecificationGroupRoutingModule],
  declarations: [
    ProjectSpecificationGroupComponent,
    ProjectSpecificationGroupDetailComponent,
    ProjectSpecificationGroupUpdateComponent,
    ProjectSpecificationGroupDeleteDialogComponent,
  ],
})
export class ProjectSpecificationGroupModule {}
