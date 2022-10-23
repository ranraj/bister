import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TaxClassComponent } from './list/tax-class.component';
import { TaxClassDetailComponent } from './detail/tax-class-detail.component';
import { TaxClassUpdateComponent } from './update/tax-class-update.component';
import { TaxClassDeleteDialogComponent } from './delete/tax-class-delete-dialog.component';
import { TaxClassRoutingModule } from './route/tax-class-routing.module';

@NgModule({
  imports: [SharedModule, TaxClassRoutingModule],
  declarations: [TaxClassComponent, TaxClassDetailComponent, TaxClassUpdateComponent, TaxClassDeleteDialogComponent],
})
export class TaxClassModule {}
