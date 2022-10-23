import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITaxClass } from '../tax-class.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tax-class.test-samples';

import { TaxClassService } from './tax-class.service';

const requireRestSample: ITaxClass = {
  ...sampleWithRequiredData,
};

describe('TaxClass Service', () => {
  let service: TaxClassService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaxClass | ITaxClass[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TaxClassService);
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

    it('should create a TaxClass', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const taxClass = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taxClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaxClass', () => {
      const taxClass = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taxClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaxClass', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaxClass', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaxClass', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaxClassToCollectionIfMissing', () => {
      it('should add a TaxClass to an empty array', () => {
        const taxClass: ITaxClass = sampleWithRequiredData;
        expectedResult = service.addTaxClassToCollectionIfMissing([], taxClass);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taxClass);
      });

      it('should not add a TaxClass to an array that contains it', () => {
        const taxClass: ITaxClass = sampleWithRequiredData;
        const taxClassCollection: ITaxClass[] = [
          {
            ...taxClass,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaxClassToCollectionIfMissing(taxClassCollection, taxClass);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaxClass to an array that doesn't contain it", () => {
        const taxClass: ITaxClass = sampleWithRequiredData;
        const taxClassCollection: ITaxClass[] = [sampleWithPartialData];
        expectedResult = service.addTaxClassToCollectionIfMissing(taxClassCollection, taxClass);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taxClass);
      });

      it('should add only unique TaxClass to an array', () => {
        const taxClassArray: ITaxClass[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taxClassCollection: ITaxClass[] = [sampleWithRequiredData];
        expectedResult = service.addTaxClassToCollectionIfMissing(taxClassCollection, ...taxClassArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taxClass: ITaxClass = sampleWithRequiredData;
        const taxClass2: ITaxClass = sampleWithPartialData;
        expectedResult = service.addTaxClassToCollectionIfMissing([], taxClass, taxClass2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taxClass);
        expect(expectedResult).toContain(taxClass2);
      });

      it('should accept null and undefined values', () => {
        const taxClass: ITaxClass = sampleWithRequiredData;
        expectedResult = service.addTaxClassToCollectionIfMissing([], null, taxClass, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taxClass);
      });

      it('should return initial array if no TaxClass is added', () => {
        const taxClassCollection: ITaxClass[] = [sampleWithRequiredData];
        expectedResult = service.addTaxClassToCollectionIfMissing(taxClassCollection, undefined, null);
        expect(expectedResult).toEqual(taxClassCollection);
      });
    });

    describe('compareTaxClass', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaxClass(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTaxClass(entity1, entity2);
        const compareResult2 = service.compareTaxClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTaxClass(entity1, entity2);
        const compareResult2 = service.compareTaxClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTaxClass(entity1, entity2);
        const compareResult2 = service.compareTaxClass(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
