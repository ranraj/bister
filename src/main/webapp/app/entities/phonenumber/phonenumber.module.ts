import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PhonenumberComponent } from './list/phonenumber.component';
import { PhonenumberDetailComponent } from './detail/phonenumber-detail.component';
import { PhonenumberUpdateComponent } from './update/phonenumber-update.component';
import { PhonenumberDeleteDialogComponent } from './delete/phonenumber-delete-dialog.component';
import { PhonenumberRoutingModule } from './route/phonenumber-routing.module';

@NgModule({
  imports: [SharedModule, PhonenumberRoutingModule],
  declarations: [PhonenumberComponent, PhonenumberDetailComponent, PhonenumberUpdateComponent, PhonenumberDeleteDialogComponent],
})
export class PhonenumberModule {}
