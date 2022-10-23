import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductAttributeComponent } from './list/product-attribute.component';
import { ProductAttributeDetailComponent } from './detail/product-attribute-detail.component';
import { ProductAttributeUpdateComponent } from './update/product-attribute-update.component';
import { ProductAttributeDeleteDialogComponent } from './delete/product-attribute-delete-dialog.component';
import { ProductAttributeRoutingModule } from './route/product-attribute-routing.module';

@NgModule({
  imports: [SharedModule, ProductAttributeRoutingModule],
  declarations: [
    ProductAttributeComponent,
    ProductAttributeDetailComponent,
    ProductAttributeUpdateComponent,
    ProductAttributeDeleteDialogComponent,
  ],
})
export class ProductAttributeModule {}
