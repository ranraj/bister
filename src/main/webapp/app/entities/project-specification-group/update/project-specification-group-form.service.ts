import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProjectSpecificationGroup, NewProjectSpecificationGroup } from '../project-specification-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectSpecificationGroup for edit and NewProjectSpecificationGroupFormGroupInput for create.
 */
type ProjectSpecificationGroupFormGroupInput = IProjectSpecificationGroup | PartialWithRequiredKeyOf<NewProjectSpecificationGroup>;

type ProjectSpecificationGroupFormDefaults = Pick<NewProjectSpecificationGroup, 'id'>;

type ProjectSpecificationGroupFormGroupContent = {
  id: FormControl<IProjectSpecificationGroup['id'] | NewProjectSpecificationGroup['id']>;
  title: FormControl<IProjectSpecificationGroup['title']>;
  slug: FormControl<IProjectSpecificationGroup['slug']>;
  description: FormControl<IProjectSpecificationGroup['description']>;
  project: FormControl<IProjectSpecificationGroup['project']>;
};

export type ProjectSpecificationGroupFormGroup = FormGroup<ProjectSpecificationGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationGroupFormService {
  createProjectSpecificationGroupFormGroup(
    projectSpecificationGroup: ProjectSpecificationGroupFormGroupInput = { id: null }
  ): ProjectSpecificationGroupFormGroup {
    const projectSpecificationGroupRawValue = {
      ...this.getFormDefaults(),
      ...projectSpecificationGroup,
    };
    return new FormGroup<ProjectSpecificationGroupFormGroupContent>({
      id: new FormControl(
        { value: projectSpecificationGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(projectSpecificationGroupRawValue.title, {
        validators: [Validators.required],
      }),
      slug: new FormControl(projectSpecificationGroupRawValue.slug),
      description: new FormControl(projectSpecificationGroupRawValue.description),
      project: new FormControl(projectSpecificationGroupRawValue.project),
    });
  }

  getProjectSpecificationGroup(form: ProjectSpecificationGroupFormGroup): IProjectSpecificationGroup | NewProjectSpecificationGroup {
    return form.getRawValue() as IProjectSpecificationGroup | NewProjectSpecificationGroup;
  }

  resetForm(form: ProjectSpecificationGroupFormGroup, projectSpecificationGroup: ProjectSpecificationGroupFormGroupInput): void {
    const projectSpecificationGroupRawValue = { ...this.getFormDefaults(), ...projectSpecificationGroup };
    form.reset(
      {
        ...projectSpecificationGroupRawValue,
        id: { value: projectSpecificationGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectSpecificationGroupFormDefaults {
    return {
      id: null,
    };
  }
}
