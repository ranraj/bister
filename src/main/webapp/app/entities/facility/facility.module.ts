import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FacilityComponent } from './list/facility.component';
import { FacilityDetailComponent } from './detail/facility-detail.component';
import { FacilityUpdateComponent } from './update/facility-update.component';
import { FacilityDeleteDialogComponent } from './delete/facility-delete-dialog.component';
import { FacilityRoutingModule } from './route/facility-routing.module';

@NgModule({
  imports: [SharedModule, FacilityRoutingModule],
  declarations: [FacilityComponent, FacilityDetailComponent, FacilityUpdateComponent, FacilityDeleteDialogComponent],
})
export class FacilityModule {}
