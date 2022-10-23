import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../facility.test-samples';

import { FacilityFormService } from './facility-form.service';

describe('Facility Form Service', () => {
  let service: FacilityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FacilityFormService);
  });

  describe('Service methods', () => {
    describe('createFacilityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFacilityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            facilityType: expect.any(Object),
            address: expect.any(Object),
            user: expect.any(Object),
            organisation: expect.any(Object),
          })
        );
      });

      it('passing IFacility should create a new form with FormGroup', () => {
        const formGroup = service.createFacilityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            facilityType: expect.any(Object),
            address: expect.any(Object),
            user: expect.any(Object),
            organisation: expect.any(Object),
          })
        );
      });
    });

    describe('getFacility', () => {
      it('should return NewFacility for default Facility initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFacilityFormGroup(sampleWithNewData);

        const facility = service.getFacility(formGroup) as any;

        expect(facility).toMatchObject(sampleWithNewData);
      });

      it('should return NewFacility for empty Facility initial value', () => {
        const formGroup = service.createFacilityFormGroup();

        const facility = service.getFacility(formGroup) as any;

        expect(facility).toMatchObject({});
      });

      it('should return IFacility', () => {
        const formGroup = service.createFacilityFormGroup(sampleWithRequiredData);

        const facility = service.getFacility(formGroup) as any;

        expect(facility).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFacility should not enable id FormControl', () => {
        const formGroup = service.createFacilityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFacility should disable id FormControl', () => {
        const formGroup = service.createFacilityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
