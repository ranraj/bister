import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEnquiry } from '../enquiry.model';
import { EnquiryService } from '../service/enquiry.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './enquiry-delete-dialog.component.html',
})
export class EnquiryDeleteDialogComponent {
  enquiry?: IEnquiry;

  constructor(protected enquiryService: EnquiryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.enquiryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
