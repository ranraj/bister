import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjectActivityComponent } from './list/project-activity.component';
import { ProjectActivityDetailComponent } from './detail/project-activity-detail.component';
import { ProjectActivityUpdateComponent } from './update/project-activity-update.component';
import { ProjectActivityDeleteDialogComponent } from './delete/project-activity-delete-dialog.component';
import { ProjectActivityRoutingModule } from './route/project-activity-routing.module';

@NgModule({
  imports: [SharedModule, ProjectActivityRoutingModule],
  declarations: [
    ProjectActivityComponent,
    ProjectActivityDetailComponent,
    ProjectActivityUpdateComponent,
    ProjectActivityDeleteDialogComponent,
  ],
})
export class ProjectActivityModule {}
