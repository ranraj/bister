import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductSpecificationGroupComponent } from './list/product-specification-group.component';
import { ProductSpecificationGroupDetailComponent } from './detail/product-specification-group-detail.component';
import { ProductSpecificationGroupUpdateComponent } from './update/product-specification-group-update.component';
import { ProductSpecificationGroupDeleteDialogComponent } from './delete/product-specification-group-delete-dialog.component';
import { ProductSpecificationGroupRoutingModule } from './route/product-specification-group-routing.module';

@NgModule({
  imports: [SharedModule, ProductSpecificationGroupRoutingModule],
  declarations: [
    ProductSpecificationGroupComponent,
    ProductSpecificationGroupDetailComponent,
    ProductSpecificationGroupUpdateComponent,
    ProductSpecificationGroupDeleteDialogComponent,
  ],
})
export class ProductSpecificationGroupModule {}
