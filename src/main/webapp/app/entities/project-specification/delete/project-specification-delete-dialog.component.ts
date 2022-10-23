import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjectSpecification } from '../project-specification.model';
import { ProjectSpecificationService } from '../service/project-specification.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './project-specification-delete-dialog.component.html',
})
export class ProjectSpecificationDeleteDialogComponent {
  projectSpecification?: IProjectSpecification;

  constructor(protected projectSpecificationService: ProjectSpecificationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projectSpecificationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
