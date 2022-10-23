import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductVariation } from '../product-variation.model';
import { ProductVariationService } from '../service/product-variation.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-variation-delete-dialog.component.html',
})
export class ProductVariationDeleteDialogComponent {
  productVariation?: IProductVariation;

  constructor(protected productVariationService: ProductVariationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productVariationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
