import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProjectType, NewProjectType } from '../project-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectType for edit and NewProjectTypeFormGroupInput for create.
 */
type ProjectTypeFormGroupInput = IProjectType | PartialWithRequiredKeyOf<NewProjectType>;

type ProjectTypeFormDefaults = Pick<NewProjectType, 'id'>;

type ProjectTypeFormGroupContent = {
  id: FormControl<IProjectType['id'] | NewProjectType['id']>;
  name: FormControl<IProjectType['name']>;
  description: FormControl<IProjectType['description']>;
};

export type ProjectTypeFormGroup = FormGroup<ProjectTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectTypeFormService {
  createProjectTypeFormGroup(projectType: ProjectTypeFormGroupInput = { id: null }): ProjectTypeFormGroup {
    const projectTypeRawValue = {
      ...this.getFormDefaults(),
      ...projectType,
    };
    return new FormGroup<ProjectTypeFormGroupContent>({
      id: new FormControl(
        { value: projectTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(projectTypeRawValue.name, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      description: new FormControl(projectTypeRawValue.description, {
        validators: [Validators.minLength(10), Validators.maxLength(100)],
      }),
    });
  }

  getProjectType(form: ProjectTypeFormGroup): IProjectType | NewProjectType {
    return form.getRawValue() as IProjectType | NewProjectType;
  }

  resetForm(form: ProjectTypeFormGroup, projectType: ProjectTypeFormGroupInput): void {
    const projectTypeRawValue = { ...this.getFormDefaults(), ...projectType };
    form.reset(
      {
        ...projectTypeRawValue,
        id: { value: projectTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectTypeFormDefaults {
    return {
      id: null,
    };
  }
}
