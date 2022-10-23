import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjectSpecificationComponent } from './list/project-specification.component';
import { ProjectSpecificationDetailComponent } from './detail/project-specification-detail.component';
import { ProjectSpecificationUpdateComponent } from './update/project-specification-update.component';
import { ProjectSpecificationDeleteDialogComponent } from './delete/project-specification-delete-dialog.component';
import { ProjectSpecificationRoutingModule } from './route/project-specification-routing.module';

@NgModule({
  imports: [SharedModule, ProjectSpecificationRoutingModule],
  declarations: [
    ProjectSpecificationComponent,
    ProjectSpecificationDetailComponent,
    ProjectSpecificationUpdateComponent,
    ProjectSpecificationDeleteDialogComponent,
  ],
})
export class ProjectSpecificationModule {}
