import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjectReviewComponent } from './list/project-review.component';
import { ProjectReviewDetailComponent } from './detail/project-review-detail.component';
import { ProjectReviewUpdateComponent } from './update/project-review-update.component';
import { ProjectReviewDeleteDialogComponent } from './delete/project-review-delete-dialog.component';
import { ProjectReviewRoutingModule } from './route/project-review-routing.module';

@NgModule({
  imports: [SharedModule, ProjectReviewRoutingModule],
  declarations: [ProjectReviewComponent, ProjectReviewDetailComponent, ProjectReviewUpdateComponent, ProjectReviewDeleteDialogComponent],
})
export class ProjectReviewModule {}
