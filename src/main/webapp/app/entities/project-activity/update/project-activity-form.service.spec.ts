import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../project-activity.test-samples';

import { ProjectActivityFormService } from './project-activity-form.service';

describe('ProjectActivity Form Service', () => {
  let service: ProjectActivityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectActivityFormService);
  });

  describe('Service methods', () => {
    describe('createProjectActivityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectActivityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            details: expect.any(Object),
            status: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });

      it('passing IProjectActivity should create a new form with FormGroup', () => {
        const formGroup = service.createProjectActivityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            details: expect.any(Object),
            status: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });
    });

    describe('getProjectActivity', () => {
      it('should return NewProjectActivity for default ProjectActivity initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProjectActivityFormGroup(sampleWithNewData);

        const projectActivity = service.getProjectActivity(formGroup) as any;

        expect(projectActivity).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectActivity for empty ProjectActivity initial value', () => {
        const formGroup = service.createProjectActivityFormGroup();

        const projectActivity = service.getProjectActivity(formGroup) as any;

        expect(projectActivity).toMatchObject({});
      });

      it('should return IProjectActivity', () => {
        const formGroup = service.createProjectActivityFormGroup(sampleWithRequiredData);

        const projectActivity = service.getProjectActivity(formGroup) as any;

        expect(projectActivity).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectActivity should not enable id FormControl', () => {
        const formGroup = service.createProjectActivityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectActivity should disable id FormControl', () => {
        const formGroup = service.createProjectActivityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
