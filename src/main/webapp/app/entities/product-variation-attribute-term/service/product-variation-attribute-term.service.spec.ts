import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../product-variation-attribute-term.test-samples';

import { ProductVariationAttributeTermService } from './product-variation-attribute-term.service';

const requireRestSample: IProductVariationAttributeTerm = {
  ...sampleWithRequiredData,
};

describe('ProductVariationAttributeTerm Service', () => {
  let service: ProductVariationAttributeTermService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductVariationAttributeTerm | IProductVariationAttributeTerm[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductVariationAttributeTermService);
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

    it('should create a ProductVariationAttributeTerm', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productVariationAttributeTerm = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productVariationAttributeTerm).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductVariationAttributeTerm', () => {
      const productVariationAttributeTerm = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productVariationAttributeTerm).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductVariationAttributeTerm', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductVariationAttributeTerm', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductVariationAttributeTerm', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductVariationAttributeTermToCollectionIfMissing', () => {
      it('should add a ProductVariationAttributeTerm to an empty array', () => {
        const productVariationAttributeTerm: IProductVariationAttributeTerm = sampleWithRequiredData;
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing([], productVariationAttributeTerm);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productVariationAttributeTerm);
      });

      it('should not add a ProductVariationAttributeTerm to an array that contains it', () => {
        const productVariationAttributeTerm: IProductVariationAttributeTerm = sampleWithRequiredData;
        const productVariationAttributeTermCollection: IProductVariationAttributeTerm[] = [
          {
            ...productVariationAttributeTerm,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing(
          productVariationAttributeTermCollection,
          productVariationAttributeTerm
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductVariationAttributeTerm to an array that doesn't contain it", () => {
        const productVariationAttributeTerm: IProductVariationAttributeTerm = sampleWithRequiredData;
        const productVariationAttributeTermCollection: IProductVariationAttributeTerm[] = [sampleWithPartialData];
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing(
          productVariationAttributeTermCollection,
          productVariationAttributeTerm
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productVariationAttributeTerm);
      });

      it('should add only unique ProductVariationAttributeTerm to an array', () => {
        const productVariationAttributeTermArray: IProductVariationAttributeTerm[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const productVariationAttributeTermCollection: IProductVariationAttributeTerm[] = [sampleWithRequiredData];
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing(
          productVariationAttributeTermCollection,
          ...productVariationAttributeTermArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productVariationAttributeTerm: IProductVariationAttributeTerm = sampleWithRequiredData;
        const productVariationAttributeTerm2: IProductVariationAttributeTerm = sampleWithPartialData;
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing(
          [],
          productVariationAttributeTerm,
          productVariationAttributeTerm2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productVariationAttributeTerm);
        expect(expectedResult).toContain(productVariationAttributeTerm2);
      });

      it('should accept null and undefined values', () => {
        const productVariationAttributeTerm: IProductVariationAttributeTerm = sampleWithRequiredData;
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing([], null, productVariationAttributeTerm, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productVariationAttributeTerm);
      });

      it('should return initial array if no ProductVariationAttributeTerm is added', () => {
        const productVariationAttributeTermCollection: IProductVariationAttributeTerm[] = [sampleWithRequiredData];
        expectedResult = service.addProductVariationAttributeTermToCollectionIfMissing(
          productVariationAttributeTermCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(productVariationAttributeTermCollection);
      });
    });

    describe('compareProductVariationAttributeTerm', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductVariationAttributeTerm(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProductVariationAttributeTerm(entity1, entity2);
        const compareResult2 = service.compareProductVariationAttributeTerm(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProductVariationAttributeTerm(entity1, entity2);
        const compareResult2 = service.compareProductVariationAttributeTerm(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProductVariationAttributeTerm(entity1, entity2);
        const compareResult2 = service.compareProductVariationAttributeTerm(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
