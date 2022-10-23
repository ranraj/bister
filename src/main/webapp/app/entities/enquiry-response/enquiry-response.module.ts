import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnquiryResponseComponent } from './list/enquiry-response.component';
import { EnquiryResponseDetailComponent } from './detail/enquiry-response-detail.component';
import { EnquiryResponseUpdateComponent } from './update/enquiry-response-update.component';
import { EnquiryResponseDeleteDialogComponent } from './delete/enquiry-response-delete-dialog.component';
import { EnquiryResponseRoutingModule } from './route/enquiry-response-routing.module';

@NgModule({
  imports: [SharedModule, EnquiryResponseRoutingModule],
  declarations: [
    EnquiryResponseComponent,
    EnquiryResponseDetailComponent,
    EnquiryResponseUpdateComponent,
    EnquiryResponseDeleteDialogComponent,
  ],
})
export class EnquiryResponseModule {}
