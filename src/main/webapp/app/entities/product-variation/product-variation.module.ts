import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductVariationComponent } from './list/product-variation.component';
import { ProductVariationDetailComponent } from './detail/product-variation-detail.component';
import { ProductVariationUpdateComponent } from './update/product-variation-update.component';
import { ProductVariationDeleteDialogComponent } from './delete/product-variation-delete-dialog.component';
import { ProductVariationRoutingModule } from './route/product-variation-routing.module';

@NgModule({
  imports: [SharedModule, ProductVariationRoutingModule],
  declarations: [
    ProductVariationComponent,
    ProductVariationDetailComponent,
    ProductVariationUpdateComponent,
    ProductVariationDeleteDialogComponent,
  ],
})
export class ProductVariationModule {}
