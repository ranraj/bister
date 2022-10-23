import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TaxRateComponent } from './list/tax-rate.component';
import { TaxRateDetailComponent } from './detail/tax-rate-detail.component';
import { TaxRateUpdateComponent } from './update/tax-rate-update.component';
import { TaxRateDeleteDialogComponent } from './delete/tax-rate-delete-dialog.component';
import { TaxRateRoutingModule } from './route/tax-rate-routing.module';

@NgModule({
  imports: [SharedModule, TaxRateRoutingModule],
  declarations: [TaxRateComponent, TaxRateDetailComponent, TaxRateUpdateComponent, TaxRateDeleteDialogComponent],
})
export class TaxRateModule {}
