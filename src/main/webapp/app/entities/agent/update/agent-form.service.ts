import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAgent, NewAgent } from '../agent.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAgent for edit and NewAgentFormGroupInput for create.
 */
type AgentFormGroupInput = IAgent | PartialWithRequiredKeyOf<NewAgent>;

type AgentFormDefaults = Pick<NewAgent, 'id'>;

type AgentFormGroupContent = {
  id: FormControl<IAgent['id'] | NewAgent['id']>;
  name: FormControl<IAgent['name']>;
  contactNumber: FormControl<IAgent['contactNumber']>;
  avatarUrl: FormControl<IAgent['avatarUrl']>;
  agentType: FormControl<IAgent['agentType']>;
  user: FormControl<IAgent['user']>;
  facility: FormControl<IAgent['facility']>;
};

export type AgentFormGroup = FormGroup<AgentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AgentFormService {
  createAgentFormGroup(agent: AgentFormGroupInput = { id: null }): AgentFormGroup {
    const agentRawValue = {
      ...this.getFormDefaults(),
      ...agent,
    };
    return new FormGroup<AgentFormGroupContent>({
      id: new FormControl(
        { value: agentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(agentRawValue.name, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      contactNumber: new FormControl(agentRawValue.contactNumber, {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(15)],
      }),
      avatarUrl: new FormControl(agentRawValue.avatarUrl),
      agentType: new FormControl(agentRawValue.agentType, {
        validators: [Validators.required],
      }),
      user: new FormControl(agentRawValue.user, {
        validators: [Validators.required],
      }),
      facility: new FormControl(agentRawValue.facility, {
        validators: [Validators.required],
      }),
    });
  }

  getAgent(form: AgentFormGroup): IAgent | NewAgent {
    return form.getRawValue() as IAgent | NewAgent;
  }

  resetForm(form: AgentFormGroup, agent: AgentFormGroupInput): void {
    const agentRawValue = { ...this.getFormDefaults(), ...agent };
    form.reset(
      {
        ...agentRawValue,
        id: { value: agentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AgentFormDefaults {
    return {
      id: null,
    };
  }
}
