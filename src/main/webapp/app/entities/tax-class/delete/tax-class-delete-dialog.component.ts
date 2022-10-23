import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITaxClass } from '../tax-class.model';
import { TaxClassService } from '../service/tax-class.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tax-class-delete-dialog.component.html',
})
export class TaxClassDeleteDialogComponent {
  taxClass?: ITaxClass;

  constructor(protected taxClassService: TaxClassService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taxClassService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
