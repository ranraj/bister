import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductActivityComponent } from './list/product-activity.component';
import { ProductActivityDetailComponent } from './detail/product-activity-detail.component';
import { ProductActivityUpdateComponent } from './update/product-activity-update.component';
import { ProductActivityDeleteDialogComponent } from './delete/product-activity-delete-dialog.component';
import { ProductActivityRoutingModule } from './route/product-activity-routing.module';

@NgModule({
  imports: [SharedModule, ProductActivityRoutingModule],
  declarations: [
    ProductActivityComponent,
    ProductActivityDetailComponent,
    ProductActivityUpdateComponent,
    ProductActivityDeleteDialogComponent,
  ],
})
export class ProductActivityModule {}
