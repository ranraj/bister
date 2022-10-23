import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjectReview } from '../project-review.model';
import { ProjectReviewService } from '../service/project-review.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './project-review-delete-dialog.component.html',
})
export class ProjectReviewDeleteDialogComponent {
  projectReview?: IProjectReview;

  constructor(protected projectReviewService: ProjectReviewService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projectReviewService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
