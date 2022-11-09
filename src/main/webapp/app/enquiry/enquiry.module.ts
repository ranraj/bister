import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnquiryComponent } from './list/enquiry.component';
import { EnquiryDetailComponent } from './detail/enquiry-detail.component';
import { EnquiryUpdateComponent } from './update/enquiry-update.component';
import { EnquiryDeleteDialogComponent } from './delete/enquiry-delete-dialog.component';
import { EnquiryRoutingModule } from './route/enquiry-routing.module';

@NgModule({
  imports: [SharedModule, EnquiryRoutingModule],
  declarations: [EnquiryComponent, EnquiryDetailComponent, EnquiryUpdateComponent, EnquiryDeleteDialogComponent],
})
export class EnquiryModule {}
