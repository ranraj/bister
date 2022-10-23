import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AgentComponent } from './list/agent.component';
import { AgentDetailComponent } from './detail/agent-detail.component';
import { AgentUpdateComponent } from './update/agent-update.component';
import { AgentDeleteDialogComponent } from './delete/agent-delete-dialog.component';
import { AgentRoutingModule } from './route/agent-routing.module';

@NgModule({
  imports: [SharedModule, AgentRoutingModule],
  declarations: [AgentComponent, AgentDetailComponent, AgentUpdateComponent, AgentDeleteDialogComponent],
})
export class AgentModule {}
