import { AgentType } from 'app/entities/enumerations/agent-type.model';

import { IAgent, NewAgent } from './agent.model';

export const sampleWithRequiredData: IAgent = {
  id: 33038,
  name: 'Rubber Sausages core',
  contactNumber: 'multi-byte',
  agentType: AgentType['COUNSELLOR'],
};

export const sampleWithPartialData: IAgent = {
  id: 1134,
  name: 'groupware',
  contactNumber: 'vortals drive B',
  agentType: AgentType['MARKETING'],
};

export const sampleWithFullData: IAgent = {
  id: 59108,
  name: 'Russian',
  contactNumber: 'enterprise',
  avatarUrl: 'Highway Fish withdrawal',
  agentType: AgentType['LEAD'],
};

export const sampleWithNewData: NewAgent = {
  name: 'Bypass',
  contactNumber: 'circuit overrid',
  agentType: AgentType['ENGINEER'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
