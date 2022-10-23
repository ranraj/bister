import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RefundComponent } from './list/refund.component';
import { RefundDetailComponent } from './detail/refund-detail.component';
import { RefundUpdateComponent } from './update/refund-update.component';
import { RefundDeleteDialogComponent } from './delete/refund-delete-dialog.component';
import { RefundRoutingModule } from './route/refund-routing.module';

@NgModule({
  imports: [SharedModule, RefundRoutingModule],
  declarations: [RefundComponent, RefundDetailComponent, RefundUpdateComponent, RefundDeleteDialogComponent],
})
export class RefundModule {}
