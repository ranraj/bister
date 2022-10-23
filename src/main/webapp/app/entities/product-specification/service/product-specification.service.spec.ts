import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductSpecification } from '../product-specification.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../product-specification.test-samples';

import { ProductSpecificationService } from './product-specification.service';

const requireRestSample: IProductSpecification = {
  ...sampleWithRequiredData,
};

describe('ProductSpecification Service', () => {
  let service: ProductSpecificationService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductSpecification | IProductSpecification[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductSpecificationService);
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

    it('should create a ProductSpecification', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productSpecification = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productSpecification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductSpecification', () => {
      const productSpecification = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productSpecification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductSpecification', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductSpecification', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductSpecification', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductSpecificationToCollectionIfMissing', () => {
      it('should add a ProductSpecification to an empty array', () => {
        const productSpecification: IProductSpecification = sampleWithRequiredData;
        expectedResult = service.addProductSpecificationToCollectionIfMissing([], productSpecification);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productSpecification);
      });

      it('should not add a ProductSpecification to an array that contains it', () => {
        const productSpecification: IProductSpecification = sampleWithRequiredData;
        const productSpecificationCollection: IProductSpecification[] = [
          {
            ...productSpecification,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductSpecificationToCollectionIfMissing(productSpecificationCollection, productSpecification);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductSpecification to an array that doesn't contain it", () => {
        const productSpecification: IProductSpecification = sampleWithRequiredData;
        const productSpecificationCollection: IProductSpecification[] = [sampleWithPartialData];
        expectedResult = service.addProductSpecificationToCollectionIfMissing(productSpecificationCollection, productSpecification);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productSpecification);
      });

      it('should add only unique ProductSpecification to an array', () => {
        const productSpecificationArray: IProductSpecification[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productSpecificationCollection: IProductSpecification[] = [sampleWithRequiredData];
        expectedResult = service.addProductSpecificationToCollectionIfMissing(productSpecificationCollection, ...productSpecificationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productSpecification: IProductSpecification = sampleWithRequiredData;
        const productSpecification2: IProductSpecification = sampleWithPartialData;
        expectedResult = service.addProductSpecificationToCollectionIfMissing([], productSpecification, productSpecification2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productSpecification);
        expect(expectedResult).toContain(productSpecification2);
      });

      it('should accept null and undefined values', () => {
        const productSpecification: IProductSpecification = sampleWithRequiredData;
        expectedResult = service.addProductSpecificationToCollectionIfMissing([], null, productSpecification, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productSpecification);
      });

      it('should return initial array if no ProductSpecification is added', () => {
        const productSpecificationCollection: IProductSpecification[] = [sampleWithRequiredData];
        expectedResult = service.addProductSpecificationToCollectionIfMissing(productSpecificationCollection, undefined, null);
        expect(expectedResult).toEqual(productSpecificationCollection);
      });
    });

    describe('compareProductSpecification', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductSpecification(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProductSpecification(entity1, entity2);
        const compareResult2 = service.compareProductSpecification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProductSpecification(entity1, entity2);
        const compareResult2 = service.compareProductSpecification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProductSpecification(entity1, entity2);
        const compareResult2 = service.compareProductSpecification(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
