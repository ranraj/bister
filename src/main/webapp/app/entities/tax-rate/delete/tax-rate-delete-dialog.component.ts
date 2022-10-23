import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITaxRate } from '../tax-rate.model';
import { TaxRateService } from '../service/tax-rate.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tax-rate-delete-dialog.component.html',
})
export class TaxRateDeleteDialogComponent {
  taxRate?: ITaxRate;

  constructor(protected taxRateService: TaxRateService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taxRateService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
