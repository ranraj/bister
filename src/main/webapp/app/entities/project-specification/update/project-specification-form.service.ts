import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProjectSpecification, NewProjectSpecification } from '../project-specification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectSpecification for edit and NewProjectSpecificationFormGroupInput for create.
 */
type ProjectSpecificationFormGroupInput = IProjectSpecification | PartialWithRequiredKeyOf<NewProjectSpecification>;

type ProjectSpecificationFormDefaults = Pick<NewProjectSpecification, 'id'>;

type ProjectSpecificationFormGroupContent = {
  id: FormControl<IProjectSpecification['id'] | NewProjectSpecification['id']>;
  title: FormControl<IProjectSpecification['title']>;
  value: FormControl<IProjectSpecification['value']>;
  description: FormControl<IProjectSpecification['description']>;
  projectSpecificationGroup: FormControl<IProjectSpecification['projectSpecificationGroup']>;
  project: FormControl<IProjectSpecification['project']>;
};

export type ProjectSpecificationFormGroup = FormGroup<ProjectSpecificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationFormService {
  createProjectSpecificationFormGroup(
    projectSpecification: ProjectSpecificationFormGroupInput = { id: null }
  ): ProjectSpecificationFormGroup {
    const projectSpecificationRawValue = {
      ...this.getFormDefaults(),
      ...projectSpecification,
    };
    return new FormGroup<ProjectSpecificationFormGroupContent>({
      id: new FormControl(
        { value: projectSpecificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(projectSpecificationRawValue.title, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      value: new FormControl(projectSpecificationRawValue.value, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(projectSpecificationRawValue.description, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      projectSpecificationGroup: new FormControl(projectSpecificationRawValue.projectSpecificationGroup),
      project: new FormControl(projectSpecificationRawValue.project, {
        validators: [Validators.required],
      }),
    });
  }

  getProjectSpecification(form: ProjectSpecificationFormGroup): IProjectSpecification | NewProjectSpecification {
    return form.getRawValue() as IProjectSpecification | NewProjectSpecification;
  }

  resetForm(form: ProjectSpecificationFormGroup, projectSpecification: ProjectSpecificationFormGroupInput): void {
    const projectSpecificationRawValue = { ...this.getFormDefaults(), ...projectSpecification };
    form.reset(
      {
        ...projectSpecificationRawValue,
        id: { value: projectSpecificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectSpecificationFormDefaults {
    return {
      id: null,
    };
  }
}
