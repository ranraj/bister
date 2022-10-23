import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductActivity } from '../product-activity.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../product-activity.test-samples';

import { ProductActivityService } from './product-activity.service';

const requireRestSample: IProductActivity = {
  ...sampleWithRequiredData,
};

describe('ProductActivity Service', () => {
  let service: ProductActivityService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductActivity | IProductActivity[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductActivityService);
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

    it('should create a ProductActivity', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productActivity = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productActivity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductActivity', () => {
      const productActivity = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productActivity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductActivity', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductActivity', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductActivity', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductActivityToCollectionIfMissing', () => {
      it('should add a ProductActivity to an empty array', () => {
        const productActivity: IProductActivity = sampleWithRequiredData;
        expectedResult = service.addProductActivityToCollectionIfMissing([], productActivity);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productActivity);
      });

      it('should not add a ProductActivity to an array that contains it', () => {
        const productActivity: IProductActivity = sampleWithRequiredData;
        const productActivityCollection: IProductActivity[] = [
          {
            ...productActivity,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductActivityToCollectionIfMissing(productActivityCollection, productActivity);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductActivity to an array that doesn't contain it", () => {
        const productActivity: IProductActivity = sampleWithRequiredData;
        const productActivityCollection: IProductActivity[] = [sampleWithPartialData];
        expectedResult = service.addProductActivityToCollectionIfMissing(productActivityCollection, productActivity);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productActivity);
      });

      it('should add only unique ProductActivity to an array', () => {
        const productActivityArray: IProductActivity[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productActivityCollection: IProductActivity[] = [sampleWithRequiredData];
        expectedResult = service.addProductActivityToCollectionIfMissing(productActivityCollection, ...productActivityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productActivity: IProductActivity = sampleWithRequiredData;
        const productActivity2: IProductActivity = sampleWithPartialData;
        expectedResult = service.addProductActivityToCollectionIfMissing([], productActivity, productActivity2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productActivity);
        expect(expectedResult).toContain(productActivity2);
      });

      it('should accept null and undefined values', () => {
        const productActivity: IProductActivity = sampleWithRequiredData;
        expectedResult = service.addProductActivityToCollectionIfMissing([], null, productActivity, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productActivity);
      });

      it('should return initial array if no ProductActivity is added', () => {
        const productActivityCollection: IProductActivity[] = [sampleWithRequiredData];
        expectedResult = service.addProductActivityToCollectionIfMissing(productActivityCollection, undefined, null);
        expect(expectedResult).toEqual(productActivityCollection);
      });
    });

    describe('compareProductActivity', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductActivity(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProductActivity(entity1, entity2);
        const compareResult2 = service.compareProductActivity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProductActivity(entity1, entity2);
        const compareResult2 = service.compareProductActivity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProductActivity(entity1, entity2);
        const compareResult2 = service.compareProductActivity(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
