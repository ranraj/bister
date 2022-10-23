import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../phonenumber.test-samples';

import { PhonenumberFormService } from './phonenumber-form.service';

describe('Phonenumber Form Service', () => {
  let service: PhonenumberFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PhonenumberFormService);
  });

  describe('Service methods', () => {
    describe('createPhonenumberFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPhonenumberFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            country: expect.any(Object),
            code: expect.any(Object),
            contactNumber: expect.any(Object),
            phonenumberType: expect.any(Object),
            user: expect.any(Object),
            organisation: expect.any(Object),
            facility: expect.any(Object),
          })
        );
      });

      it('passing IPhonenumber should create a new form with FormGroup', () => {
        const formGroup = service.createPhonenumberFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            country: expect.any(Object),
            code: expect.any(Object),
            contactNumber: expect.any(Object),
            phonenumberType: expect.any(Object),
            user: expect.any(Object),
            organisation: expect.any(Object),
            facility: expect.any(Object),
          })
        );
      });
    });

    describe('getPhonenumber', () => {
      it('should return NewPhonenumber for default Phonenumber initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPhonenumberFormGroup(sampleWithNewData);

        const phonenumber = service.getPhonenumber(formGroup) as any;

        expect(phonenumber).toMatchObject(sampleWithNewData);
      });

      it('should return NewPhonenumber for empty Phonenumber initial value', () => {
        const formGroup = service.createPhonenumberFormGroup();

        const phonenumber = service.getPhonenumber(formGroup) as any;

        expect(phonenumber).toMatchObject({});
      });

      it('should return IPhonenumber', () => {
        const formGroup = service.createPhonenumberFormGroup(sampleWithRequiredData);

        const phonenumber = service.getPhonenumber(formGroup) as any;

        expect(phonenumber).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPhonenumber should not enable id FormControl', () => {
        const formGroup = service.createPhonenumberFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPhonenumber should disable id FormControl', () => {
        const formGroup = service.createPhonenumberFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
