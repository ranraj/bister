import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../project-review.test-samples';

import { ProjectReviewFormService } from './project-review-form.service';

describe('ProjectReview Form Service', () => {
  let service: ProjectReviewFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectReviewFormService);
  });

  describe('Service methods', () => {
    describe('createProjectReviewFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectReviewFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reviewerName: expect.any(Object),
            reviewerEmail: expect.any(Object),
            review: expect.any(Object),
            rating: expect.any(Object),
            status: expect.any(Object),
            reviewerId: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });

      it('passing IProjectReview should create a new form with FormGroup', () => {
        const formGroup = service.createProjectReviewFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reviewerName: expect.any(Object),
            reviewerEmail: expect.any(Object),
            review: expect.any(Object),
            rating: expect.any(Object),
            status: expect.any(Object),
            reviewerId: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });
    });

    describe('getProjectReview', () => {
      it('should return NewProjectReview for default ProjectReview initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProjectReviewFormGroup(sampleWithNewData);

        const projectReview = service.getProjectReview(formGroup) as any;

        expect(projectReview).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectReview for empty ProjectReview initial value', () => {
        const formGroup = service.createProjectReviewFormGroup();

        const projectReview = service.getProjectReview(formGroup) as any;

        expect(projectReview).toMatchObject({});
      });

      it('should return IProjectReview', () => {
        const formGroup = service.createProjectReviewFormGroup(sampleWithRequiredData);

        const projectReview = service.getProjectReview(formGroup) as any;

        expect(projectReview).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectReview should not enable id FormControl', () => {
        const formGroup = service.createProjectReviewFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectReview should disable id FormControl', () => {
        const formGroup = service.createProjectReviewFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
