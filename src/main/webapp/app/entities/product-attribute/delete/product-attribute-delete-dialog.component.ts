import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductAttribute } from '../product-attribute.model';
import { ProductAttributeService } from '../service/product-attribute.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-attribute-delete-dialog.component.html',
})
export class ProductAttributeDeleteDialogComponent {
  productAttribute?: IProductAttribute;

  constructor(protected productAttributeService: ProductAttributeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productAttributeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
