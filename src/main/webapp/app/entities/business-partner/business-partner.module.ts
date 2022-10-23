import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BusinessPartnerComponent } from './list/business-partner.component';
import { BusinessPartnerDetailComponent } from './detail/business-partner-detail.component';
import { BusinessPartnerUpdateComponent } from './update/business-partner-update.component';
import { BusinessPartnerDeleteDialogComponent } from './delete/business-partner-delete-dialog.component';
import { BusinessPartnerRoutingModule } from './route/business-partner-routing.module';

@NgModule({
  imports: [SharedModule, BusinessPartnerRoutingModule],
  declarations: [
    BusinessPartnerComponent,
    BusinessPartnerDetailComponent,
    BusinessPartnerUpdateComponent,
    BusinessPartnerDeleteDialogComponent,
  ],
})
export class BusinessPartnerModule {}
