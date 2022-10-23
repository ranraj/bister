import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProjectReview, NewProjectReview } from '../project-review.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectReview for edit and NewProjectReviewFormGroupInput for create.
 */
type ProjectReviewFormGroupInput = IProjectReview | PartialWithRequiredKeyOf<NewProjectReview>;

type ProjectReviewFormDefaults = Pick<NewProjectReview, 'id'>;

type ProjectReviewFormGroupContent = {
  id: FormControl<IProjectReview['id'] | NewProjectReview['id']>;
  reviewerName: FormControl<IProjectReview['reviewerName']>;
  reviewerEmail: FormControl<IProjectReview['reviewerEmail']>;
  review: FormControl<IProjectReview['review']>;
  rating: FormControl<IProjectReview['rating']>;
  status: FormControl<IProjectReview['status']>;
  reviewerId: FormControl<IProjectReview['reviewerId']>;
  project: FormControl<IProjectReview['project']>;
};

export type ProjectReviewFormGroup = FormGroup<ProjectReviewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectReviewFormService {
  createProjectReviewFormGroup(projectReview: ProjectReviewFormGroupInput = { id: null }): ProjectReviewFormGroup {
    const projectReviewRawValue = {
      ...this.getFormDefaults(),
      ...projectReview,
    };
    return new FormGroup<ProjectReviewFormGroupContent>({
      id: new FormControl(
        { value: projectReviewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      reviewerName: new FormControl(projectReviewRawValue.reviewerName, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      reviewerEmail: new FormControl(projectReviewRawValue.reviewerEmail, {
        validators: [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')],
      }),
      review: new FormControl(projectReviewRawValue.review, {
        validators: [Validators.required, Validators.minLength(20), Validators.maxLength(1000)],
      }),
      rating: new FormControl(projectReviewRawValue.rating, {
        validators: [Validators.required],
      }),
      status: new FormControl(projectReviewRawValue.status, {
        validators: [Validators.required],
      }),
      reviewerId: new FormControl(projectReviewRawValue.reviewerId, {
        validators: [Validators.required],
      }),
      project: new FormControl(projectReviewRawValue.project, {
        validators: [Validators.required],
      }),
    });
  }

  getProjectReview(form: ProjectReviewFormGroup): IProjectReview | NewProjectReview {
    return form.getRawValue() as IProjectReview | NewProjectReview;
  }

  resetForm(form: ProjectReviewFormGroup, projectReview: ProjectReviewFormGroupInput): void {
    const projectReviewRawValue = { ...this.getFormDefaults(), ...projectReview };
    form.reset(
      {
        ...projectReviewRawValue,
        id: { value: projectReviewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProjectReviewFormDefaults {
    return {
      id: null,
    };
  }
}
