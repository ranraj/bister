import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBusinessPartner } from '../business-partner.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../business-partner.test-samples';

import { BusinessPartnerService } from './business-partner.service';

const requireRestSample: IBusinessPartner = {
  ...sampleWithRequiredData,
};

describe('BusinessPartner Service', () => {
  let service: BusinessPartnerService;
  let httpMock: HttpTestingController;
  let expectedResult: IBusinessPartner | IBusinessPartner[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BusinessPartnerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BusinessPartner', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const businessPartner = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(businessPartner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BusinessPartner', () => {
      const businessPartner = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(businessPartner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BusinessPartner', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BusinessPartner', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BusinessPartner', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBusinessPartnerToCollectionIfMissing', () => {
      it('should add a BusinessPartner to an empty array', () => {
        const businessPartner: IBusinessPartner = sampleWithRequiredData;
        expectedResult = service.addBusinessPartnerToCollectionIfMissing([], businessPartner);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businessPartner);
      });

      it('should not add a BusinessPartner to an array that contains it', () => {
        const businessPartner: IBusinessPartner = sampleWithRequiredData;
        const businessPartnerCollection: IBusinessPartner[] = [
          {
            ...businessPartner,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBusinessPartnerToCollectionIfMissing(businessPartnerCollection, businessPartner);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BusinessPartner to an array that doesn't contain it", () => {
        const businessPartner: IBusinessPartner = sampleWithRequiredData;
        const businessPartnerCollection: IBusinessPartner[] = [sampleWithPartialData];
        expectedResult = service.addBusinessPartnerToCollectionIfMissing(businessPartnerCollection, businessPartner);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businessPartner);
      });

      it('should add only unique BusinessPartner to an array', () => {
        const businessPartnerArray: IBusinessPartner[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const businessPartnerCollection: IBusinessPartner[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessPartnerToCollectionIfMissing(businessPartnerCollection, ...businessPartnerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const businessPartner: IBusinessPartner = sampleWithRequiredData;
        const businessPartner2: IBusinessPartner = sampleWithPartialData;
        expectedResult = service.addBusinessPartnerToCollectionIfMissing([], businessPartner, businessPartner2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businessPartner);
        expect(expectedResult).toContain(businessPartner2);
      });

      it('should accept null and undefined values', () => {
        const businessPartner: IBusinessPartner = sampleWithRequiredData;
        expectedResult = service.addBusinessPartnerToCollectionIfMissing([], null, businessPartner, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businessPartner);
      });

      it('should return initial array if no BusinessPartner is added', () => {
        const businessPartnerCollection: IBusinessPartner[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessPartnerToCollectionIfMissing(businessPartnerCollection, undefined, null);
        expect(expectedResult).toEqual(businessPartnerCollection);
      });
    });

    describe('compareBusinessPartner', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBusinessPartner(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBusinessPartner(entity1, entity2);
        const compareResult2 = service.compareBusinessPartner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBusinessPartner(entity1, entity2);
        const compareResult2 = service.compareBusinessPartner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBusinessPartner(entity1, entity2);
        const compareResult2 = service.compareBusinessPartner(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
