import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductSpecification } from '../product-specification.model';
import { ProductSpecificationService } from '../service/product-specification.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-specification-delete-dialog.component.html',
})
export class ProductSpecificationDeleteDialogComponent {
  productSpecification?: IProductSpecification;

  constructor(protected productSpecificationService: ProductSpecificationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productSpecificationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
