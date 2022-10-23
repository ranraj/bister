import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjectActivity } from '../project-activity.model';
import { ProjectActivityService } from '../service/project-activity.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './project-activity-delete-dialog.component.html',
})
export class ProjectActivityDeleteDialogComponent {
  projectActivity?: IProjectActivity;

  constructor(protected projectActivityService: ProjectActivityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projectActivityService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
