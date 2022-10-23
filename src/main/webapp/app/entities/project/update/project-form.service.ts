import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProject, NewProject } from '../project.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProject for edit and NewProjectFormGroupInput for create.
 */
type ProjectFormGroupInput = IProject | PartialWithRequiredKeyOf<NewProject>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProject | NewProject> = Omit<T, 'dateCreated'> & {
  dateCreated?: string | null;
};

type ProjectFormRawValue = FormValueOf<IProject>;

type NewProjectFormRawValue = FormValueOf<NewProject>;

type ProjectFormDefaults = Pick<NewProject, 'id' | 'published' | 'dateCreated' | 'categories'>;

type ProjectFormGroupContent = {
  id: FormControl<ProjectFormRawValue['id'] | NewProject['id']>;
  name: FormControl<ProjectFormRawValue['name']>;
  slug: FormControl<ProjectFormRawValue['slug']>;
  description: FormControl<ProjectFormRawValue['description']>;
  shortDescription: FormControl<ProjectFormRawValue['shortDescription']>;
  regularPrice: FormControl<ProjectFormRawValue['regularPrice']>;
  salePrice: FormControl<ProjectFormRawValue['salePrice']>;
  published: FormControl<ProjectFormRawValue['published']>;
  dateCreated: FormControl<ProjectFormRawValue['dateCreated']>;
  dateModified: FormControl<ProjectFormRawValue['dateModified']>;
  projectStatus: FormControl<ProjectFormRawValue['projectStatus']>;
  sharableHash: FormControl<ProjectFormRawValue['sharableHash']>;
  estimatedBudget: FormControl<ProjectFormRawValue['estimatedBudget']>;
  address: FormControl<ProjectFormRawValue['address']>;
  projectType: FormControl<ProjectFormRawValue['projectType']>;
  categories: FormControl<ProjectFormRawValue['categories']>;
};

export type ProjectFormGroup = FormGroup<ProjectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectFormService {
  createProjectFormGroup(project: ProjectFormGroupInput = { id: null }): ProjectFormGroup {
    const projectRawValue = this.convertProjectToProjectRawValue({
      ...this.getFormDefaults(),
      ...project,
    });
    return new FormGroup<ProjectFormGroupContent>({
      id: new FormControl(
        { value: projectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(projectRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(250)],
      }),
      slug: new FormControl(projectRawValue.slug, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(projectRawValue.description, {
        validators: [Validators.required, Validators.minLength(20), Validators.maxLength(1000)],
      }),
      shortDescription: new FormControl(projectRawValue.shortDescription, {
        validators: [Validators.minLength(20), Validators.maxLength(50)],
      }),
      regularPrice: new FormControl(projectRawValue.regularPrice, {
        validators: [Validators.required],
      }),
      salePrice: new FormControl(projectRawValue.salePrice, {
        validators: [Validators.required],
      }),
      published: new FormControl(projectRawValue.published, {
        validators: [Validators.required],
      }),
      dateCreated: new FormControl(projectRawValue.dateCreated, {
        validators: [Validators.required],
      }),
      dateModified: new FormControl(projectRawValue.dateModified, {
        validators: [Validators.required],
      }),
      projectStatus: new FormControl(projectRawValue.projectStatus, {
        validators: [Validators.required],
      }),
      sharableHash: new FormControl(projectRawValue.sharableHash),
      estimatedBudget: new FormControl(projectRawValue.estimatedBudget, {
        validators: [Validators.required],
      }),
      address: new FormControl(projectRawValue.address, {
        validators: [Validators.required],
      }),
      projectType: new FormControl(projectRawValue.projectType, {
        validators: [Validators.required],
      }),
      categories: new FormControl(projectRawValue.categories ?? []),
    });
  }

  getProject(form: ProjectFormGroup): IProject | NewProject {
    return this.convertProjectRawValueToProject(form.getRawValue() as ProjectFormRawValue | NewProjectFormRawValue);
  }

  resetForm(form: ProjectFormGroup, project: ProjectFormGroupInput): void {
    const projectRawValue = this.convertProjectToProjectRawValue({ ...this.getFormDefaults(), ...project });
    form.reset(
      {
        ...projectRawValue,
        id: { value: projectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      published: false,
      dateCreated: currentTime,
      categories: [],
    };
  }

  private convertProjectRawValueToProject(rawProject: ProjectFormRawValue | NewProjectFormRawValue): IProject | NewProject {
    return {
      ...rawProject,
      dateCreated: dayjs(rawProject.dateCreated, DATE_TIME_FORMAT),
    };
  }

  private convertProjectToProjectRawValue(
    project: IProject | (Partial<NewProject> & ProjectFormDefaults)
  ): ProjectFormRawValue | PartialWithRequiredKeyOf<NewProjectFormRawValue> {
    return {
      ...project,
      dateCreated: project.dateCreated ? project.dateCreated.format(DATE_TIME_FORMAT) : undefined,
      categories: project.categories ?? [],
    };
  }
}
