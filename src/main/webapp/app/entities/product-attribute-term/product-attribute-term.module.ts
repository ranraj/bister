import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductAttributeTermComponent } from './list/product-attribute-term.component';
import { ProductAttributeTermDetailComponent } from './detail/product-attribute-term-detail.component';
import { ProductAttributeTermUpdateComponent } from './update/product-attribute-term-update.component';
import { ProductAttributeTermDeleteDialogComponent } from './delete/product-attribute-term-delete-dialog.component';
import { ProductAttributeTermRoutingModule } from './route/product-attribute-term-routing.module';

@NgModule({
  imports: [SharedModule, ProductAttributeTermRoutingModule],
  declarations: [
    ProductAttributeTermComponent,
    ProductAttributeTermDetailComponent,
    ProductAttributeTermUpdateComponent,
    ProductAttributeTermDeleteDialogComponent,
  ],
})
export class ProductAttributeTermModule {}
