import { IUser } from 'app/entities/user/user.model';
import { IFacility } from 'app/entities/facility/facility.model';
import { AgentType } from 'app/entities/enumerations/agent-type.model';

export interface IAgent {
  id: number;
  name?: string | null;
  contactNumber?: string | null;
  avatarUrl?: string | null;
  agentType?: AgentType | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  facility?: Pick<IFacility, 'id' | 'name'> | null;
}

export type NewAgent = Omit<IAgent, 'id'> & { id: null };
