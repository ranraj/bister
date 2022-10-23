import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductActivity } from '../product-activity.model';
import { ProductActivityService } from '../service/product-activity.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-activity-delete-dialog.component.html',
})
export class ProductActivityDeleteDialogComponent {
  productActivity?: IProductActivity;

  constructor(protected productActivityService: ProductActivityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productActivityService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
