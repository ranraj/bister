import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEnquiryResponse } from '../enquiry-response.model';
import { EnquiryResponseService } from '../service/enquiry-response.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './enquiry-response-delete-dialog.component.html',
})
export class EnquiryResponseDeleteDialogComponent {
  enquiryResponse?: IEnquiryResponse;

  constructor(protected enquiryResponseService: EnquiryResponseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.enquiryResponseService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
