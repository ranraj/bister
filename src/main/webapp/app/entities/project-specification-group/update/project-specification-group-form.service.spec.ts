import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../project-specification-group.test-samples';

import { ProjectSpecificationGroupFormService } from './project-specification-group-form.service';

describe('ProjectSpecificationGroup Form Service', () => {
  let service: ProjectSpecificationGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectSpecificationGroupFormService);
  });

  describe('Service methods', () => {
    describe('createProjectSpecificationGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });

      it('passing IProjectSpecificationGroup should create a new form with FormGroup', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            project: expect.any(Object),
          })
        );
      });
    });

    describe('getProjectSpecificationGroup', () => {
      it('should return NewProjectSpecificationGroup for default ProjectSpecificationGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProjectSpecificationGroupFormGroup(sampleWithNewData);

        const projectSpecificationGroup = service.getProjectSpecificationGroup(formGroup) as any;

        expect(projectSpecificationGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectSpecificationGroup for empty ProjectSpecificationGroup initial value', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup();

        const projectSpecificationGroup = service.getProjectSpecificationGroup(formGroup) as any;

        expect(projectSpecificationGroup).toMatchObject({});
      });

      it('should return IProjectSpecificationGroup', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup(sampleWithRequiredData);

        const projectSpecificationGroup = service.getProjectSpecificationGroup(formGroup) as any;

        expect(projectSpecificationGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectSpecificationGroup should not enable id FormControl', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectSpecificationGroup should disable id FormControl', () => {
        const formGroup = service.createProjectSpecificationGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
