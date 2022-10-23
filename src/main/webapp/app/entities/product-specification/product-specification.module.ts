import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductSpecificationComponent } from './list/product-specification.component';
import { ProductSpecificationDetailComponent } from './detail/product-specification-detail.component';
import { ProductSpecificationUpdateComponent } from './update/product-specification-update.component';
import { ProductSpecificationDeleteDialogComponent } from './delete/product-specification-delete-dialog.component';
import { ProductSpecificationRoutingModule } from './route/product-specification-routing.module';

@NgModule({
  imports: [SharedModule, ProductSpecificationRoutingModule],
  declarations: [
    ProductSpecificationComponent,
    ProductSpecificationDetailComponent,
    ProductSpecificationUpdateComponent,
    ProductSpecificationDeleteDialogComponent,
  ],
})
export class ProductSpecificationModule {}
