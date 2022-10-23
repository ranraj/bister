import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPhonenumber } from '../phonenumber.model';
import { PhonenumberService } from '../service/phonenumber.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './phonenumber-delete-dialog.component.html',
})
export class PhonenumberDeleteDialogComponent {
  phonenumber?: IPhonenumber;

  constructor(protected phonenumberService: PhonenumberService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.phonenumberService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
