import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBusinessPartner } from '../business-partner.model';
import { BusinessPartnerService } from '../service/business-partner.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './business-partner-delete-dialog.component.html',
})
export class BusinessPartnerDeleteDialogComponent {
  businessPartner?: IBusinessPartner;

  constructor(protected businessPartnerService: BusinessPartnerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.businessPartnerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
