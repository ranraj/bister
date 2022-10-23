import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PromotionComponent } from './list/promotion.component';
import { PromotionDetailComponent } from './detail/promotion-detail.component';
import { PromotionUpdateComponent } from './update/promotion-update.component';
import { PromotionDeleteDialogComponent } from './delete/promotion-delete-dialog.component';
import { PromotionRoutingModule } from './route/promotion-routing.module';

@NgModule({
  imports: [SharedModule, PromotionRoutingModule],
  declarations: [PromotionComponent, PromotionDetailComponent, PromotionUpdateComponent, PromotionDeleteDialogComponent],
})
export class PromotionModule {}
