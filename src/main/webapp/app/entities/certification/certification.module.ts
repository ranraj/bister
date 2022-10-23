import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CertificationComponent } from './list/certification.component';
import { CertificationDetailComponent } from './detail/certification-detail.component';
import { CertificationUpdateComponent } from './update/certification-update.component';
import { CertificationDeleteDialogComponent } from './delete/certification-delete-dialog.component';
import { CertificationRoutingModule } from './route/certification-routing.module';

@NgModule({
  imports: [SharedModule, CertificationRoutingModule],
  declarations: [CertificationComponent, CertificationDetailComponent, CertificationUpdateComponent, CertificationDeleteDialogComponent],
})
export class CertificationModule {}
