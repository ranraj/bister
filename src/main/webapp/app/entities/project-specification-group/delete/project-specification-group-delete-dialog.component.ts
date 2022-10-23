import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjectSpecificationGroup } from '../project-specification-group.model';
import { ProjectSpecificationGroupService } from '../service/project-specification-group.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './project-specification-group-delete-dialog.component.html',
})
export class ProjectSpecificationGroupDeleteDialogComponent {
  projectSpecificationGroup?: IProjectSpecificationGroup;

  constructor(protected projectSpecificationGroupService: ProjectSpecificationGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projectSpecificationGroupService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
