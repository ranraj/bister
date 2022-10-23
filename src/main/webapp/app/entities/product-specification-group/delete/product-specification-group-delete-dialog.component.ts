import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductSpecificationGroup } from '../product-specification-group.model';
import { ProductSpecificationGroupService } from '../service/product-specification-group.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './product-specification-group-delete-dialog.component.html',
})
export class ProductSpecificationGroupDeleteDialogComponent {
  productSpecificationGroup?: IProductSpecificationGroup;

  constructor(protected productSpecificationGroupService: ProductSpecificationGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productSpecificationGroupService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
