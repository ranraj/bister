import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProjectActivity, NewProjectActivity } from '../project-activity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectActivity for edit and NewProjectActivityFormGroupInput for create.
 */
type ProjectActivityFormGroupInput = IProjectActivity | PartialWithRequiredKeyOf<NewProjectActivity>;

type ProjectActivityFormDefaults = Pick<NewProjectActivity, 'id'>;

type ProjectActivityFormGroupContent = {
  id: FormControl<IProjectActivity['id'] | NewProjectActivity['id']>;
  title: FormControl<IProjectActivity['title']>;
  details: FormControl<IProjectActivity['details']>;
  status: FormControl<IProjectActivity['status']>;
  project: FormControl<IProjectActivity['project']>;
};

export type ProjectActivityFormGroup = FormGroup<ProjectActivityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectActivityFormService {
  createProjectActivityFormGroup(projectActivity: ProjectActivityFormGroupInput = { id: null }): ProjectActivityFormGroup {
    const projectActivityRawValue = {
      ...this.getFormDefaults(),
      ...projectActivity,
    };
    return new FormGroup<ProjectActivityFormGroupContent>({
      id: new FormControl(
        { value: projectActivityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(projectActivityRawValue.title, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      details: new FormControl(projectActivityRawValue.details, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      status: new FormControl(projectActivityRawValue.status, {
        validators: [Validators.required],
      }),
      project: new FormControl(projectActivityRawValue.project, {
        validators: [Validators.required],
      }),
    });
  }

  getProjectActivity(form: ProjectActivityFormGroup): IProjectActivity | NewProjectActivity {
    return form.getRawValue() as IProjectActivity | NewProjectActivity;
  }

  resetForm(form: ProjectActivityFormGroup, projectActivity: ProjectActivityFormGroupInput): void {
    const projectActivityRawValue = { ...this.getFormDefaults(), ...projectActivity };
    form.reset(
      {
        ...projectActivityRawValue,
        id: { value: projectActivityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectActivityFormDefaults {
    return {
      id: null,
    };
  }
}
