import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';
import { ProductVariationAttributeTermService } from '../service/product-variation-attribute-term.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-variation-attribute-term-delete-dialog.component.html',
})
export class ProductVariationAttributeTermDeleteDialogComponent {
  productVariationAttributeTerm?: IProductVariationAttributeTerm;

  constructor(
    protected productVariationAttributeTermService: ProductVariationAttributeTermService,
    protected activeModal: NgbActiveModal
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productVariationAttributeTermService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
