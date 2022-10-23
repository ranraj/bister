import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../project-type.test-samples';

import { ProjectTypeFormService } from './project-type-form.service';

describe('ProjectType Form Service', () => {
  let service: ProjectTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectTypeFormService);
  });

  describe('Service methods', () => {
    describe('createProjectTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });

      it('passing IProjectType should create a new form with FormGroup', () => {
        const formGroup = service.createProjectTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });
    });

    describe('getProjectType', () => {
      it('should return NewProjectType for default ProjectType initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProjectTypeFormGroup(sampleWithNewData);

        const projectType = service.getProjectType(formGroup) as any;

        expect(projectType).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectType for empty ProjectType initial value', () => {
        const formGroup = service.createProjectTypeFormGroup();

        const projectType = service.getProjectType(formGroup) as any;

        expect(projectType).toMatchObject({});
      });

      it('should return IProjectType', () => {
        const formGroup = service.createProjectTypeFormGroup(sampleWithRequiredData);

        const projectType = service.getProjectType(formGroup) as any;

        expect(projectType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectType should not enable id FormControl', () => {
        const formGroup = service.createProjectTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectType should disable id FormControl', () => {
        const formGroup = service.createProjectTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
