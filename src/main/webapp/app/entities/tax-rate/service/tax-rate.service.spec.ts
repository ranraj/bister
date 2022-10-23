import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITaxRate } from '../tax-rate.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tax-rate.test-samples';

import { TaxRateService } from './tax-rate.service';

const requireRestSample: ITaxRate = {
  ...sampleWithRequiredData,
};

describe('TaxRate Service', () => {
  let service: TaxRateService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaxRate | ITaxRate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TaxRateService);
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

    it('should create a TaxRate', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const taxRate = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taxRate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaxRate', () => {
      const taxRate = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taxRate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaxRate', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaxRate', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaxRate', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaxRateToCollectionIfMissing', () => {
      it('should add a TaxRate to an empty array', () => {
        const taxRate: ITaxRate = sampleWithRequiredData;
        expectedResult = service.addTaxRateToCollectionIfMissing([], taxRate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taxRate);
      });

      it('should not add a TaxRate to an array that contains it', () => {
        const taxRate: ITaxRate = sampleWithRequiredData;
        const taxRateCollection: ITaxRate[] = [
          {
            ...taxRate,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaxRateToCollectionIfMissing(taxRateCollection, taxRate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaxRate to an array that doesn't contain it", () => {
        const taxRate: ITaxRate = sampleWithRequiredData;
        const taxRateCollection: ITaxRate[] = [sampleWithPartialData];
        expectedResult = service.addTaxRateToCollectionIfMissing(taxRateCollection, taxRate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taxRate);
      });

      it('should add only unique TaxRate to an array', () => {
        const taxRateArray: ITaxRate[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taxRateCollection: ITaxRate[] = [sampleWithRequiredData];
        expectedResult = service.addTaxRateToCollectionIfMissing(taxRateCollection, ...taxRateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taxRate: ITaxRate = sampleWithRequiredData;
        const taxRate2: ITaxRate = sampleWithPartialData;
        expectedResult = service.addTaxRateToCollectionIfMissing([], taxRate, taxRate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taxRate);
        expect(expectedResult).toContain(taxRate2);
      });

      it('should accept null and undefined values', () => {
        const taxRate: ITaxRate = sampleWithRequiredData;
        expectedResult = service.addTaxRateToCollectionIfMissing([], null, taxRate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taxRate);
      });

      it('should return initial array if no TaxRate is added', () => {
        const taxRateCollection: ITaxRate[] = [sampleWithRequiredData];
        expectedResult = service.addTaxRateToCollectionIfMissing(taxRateCollection, undefined, null);
        expect(expectedResult).toEqual(taxRateCollection);
      });
    });

    describe('compareTaxRate', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaxRate(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTaxRate(entity1, entity2);
        const compareResult2 = service.compareTaxRate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTaxRate(entity1, entity2);
        const compareResult2 = service.compareTaxRate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTaxRate(entity1, entity2);
        const compareResult2 = service.compareTaxRate(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
