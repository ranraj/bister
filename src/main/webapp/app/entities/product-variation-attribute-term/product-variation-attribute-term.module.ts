import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductVariationAttributeTermComponent } from './list/product-variation-attribute-term.component';
import { ProductVariationAttributeTermDetailComponent } from './detail/product-variation-attribute-term-detail.component';
import { ProductVariationAttributeTermUpdateComponent } from './update/product-variation-attribute-term-update.component';
import { ProductVariationAttributeTermDeleteDialogComponent } from './delete/product-variation-attribute-term-delete-dialog.component';
import { ProductVariationAttributeTermRoutingModule } from './route/product-variation-attribute-term-routing.module';

@NgModule({
  imports: [SharedModule, ProductVariationAttributeTermRoutingModule],
  declarations: [
    ProductVariationAttributeTermComponent,
    ProductVariationAttributeTermDetailComponent,
    ProductVariationAttributeTermUpdateComponent,
    ProductVariationAttributeTermDeleteDialogComponent,
  ],
})
export class ProductVariationAttributeTermModule {}
