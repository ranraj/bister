import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductReviewComponent } from './list/product-review.component';
import { ProductReviewDetailComponent } from './detail/product-review-detail.component';
import { ProductReviewUpdateComponent } from './update/product-review-update.component';
import { ProductReviewDeleteDialogComponent } from './delete/product-review-delete-dialog.component';
import { ProductReviewRoutingModule } from './route/product-review-routing.module';

@NgModule({
  imports: [SharedModule, ProductReviewRoutingModule],
  declarations: [ProductReviewComponent, ProductReviewDetailComponent, ProductReviewUpdateComponent, ProductReviewDeleteDialogComponent],
})
export class ProductReviewModule {}
