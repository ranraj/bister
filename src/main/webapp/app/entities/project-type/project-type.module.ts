import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjectTypeComponent } from './list/project-type.component';
import { ProjectTypeDetailComponent } from './detail/project-type-detail.component';
import { ProjectTypeUpdateComponent } from './update/project-type-update.component';
import { ProjectTypeDeleteDialogComponent } from './delete/project-type-delete-dialog.component';
import { ProjectTypeRoutingModule } from './route/project-type-routing.module';

@NgModule({
  imports: [SharedModule, ProjectTypeRoutingModule],
  declarations: [ProjectTypeComponent, ProjectTypeDetailComponent, ProjectTypeUpdateComponent, ProjectTypeDeleteDialogComponent],
})
export class ProjectTypeModule {}
