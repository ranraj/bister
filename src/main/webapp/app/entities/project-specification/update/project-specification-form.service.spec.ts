import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../project-specification.test-samples';

import { ProjectSpecificationFormService } from './project-specification-form.service';

describe('ProjectSpecification Form Service', () => {
  let service: ProjectSpecificationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectSpecificationFormService);
  });

  describe('Service methods', () => {
    describe('createProjectSpecificationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectSpecificationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            projectSpecificationGroup: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });

      it('passing IProjectSpecification should create a new form with FormGroup', () => {
        const formGroup = service.createProjectSpecificationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            projectSpecificationGroup: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });
    });

    describe('getProjectSpecification', () => {
      it('should return NewProjectSpecification for default ProjectSpecification initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProjectSpecificationFormGroup(sampleWithNewData);

        const projectSpecification = service.getProjectSpecification(formGroup) as any;

        expect(projectSpecification).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectSpecification for empty ProjectSpecification initial value', () => {
        const formGroup = service.createProjectSpecificationFormGroup();

        const projectSpecification = service.getProjectSpecification(formGroup) as any;

        expect(projectSpecification).toMatchObject({});
      });

      it('should return IProjectSpecification', () => {
        const formGroup = service.createProjectSpecificationFormGroup(sampleWithRequiredData);

        const projectSpecification = service.getProjectSpecification(formGroup) as any;

        expect(projectSpecification).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectSpecification should not enable id FormControl', () => {
        const formGroup = service.createProjectSpecificationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectSpecification should disable id FormControl', () => {
        const formGroup = service.createProjectSpecificationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
