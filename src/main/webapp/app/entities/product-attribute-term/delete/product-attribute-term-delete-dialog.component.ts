import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductAttributeTerm } from '../product-attribute-term.model';
import { ProductAttributeTermService } from '../service/product-attribute-term.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-attribute-term-delete-dialog.component.html',
})
export class ProductAttributeTermDeleteDialogComponent {
  productAttributeTerm?: IProductAttributeTerm;

  constructor(protected productAttributeTermService: ProductAttributeTermService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productAttributeTermService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
