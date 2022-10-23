import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductAttribute } from '../product-attribute.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../product-attribute.test-samples';

import { ProductAttributeService } from './product-attribute.service';

const requireRestSample: IProductAttribute = {
  ...sampleWithRequiredData,
};

describe('ProductAttribute Service', () => {
  let service: ProductAttributeService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductAttribute | IProductAttribute[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductAttributeService);
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

    it('should create a ProductAttribute', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productAttribute = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductAttribute', () => {
      const productAttribute = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductAttribute', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductAttribute', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductAttribute', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductAttributeToCollectionIfMissing', () => {
      it('should add a ProductAttribute to an empty array', () => {
        const productAttribute: IProductAttribute = sampleWithRequiredData;
        expectedResult = service.addProductAttributeToCollectionIfMissing([], productAttribute);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productAttribute);
      });

      it('should not add a ProductAttribute to an array that contains it', () => {
        const productAttribute: IProductAttribute = sampleWithRequiredData;
        const productAttributeCollection: IProductAttribute[] = [
          {
            ...productAttribute,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductAttributeToCollectionIfMissing(productAttributeCollection, productAttribute);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductAttribute to an array that doesn't contain it", () => {
        const productAttribute: IProductAttribute = sampleWithRequiredData;
        const productAttributeCollection: IProductAttribute[] = [sampleWithPartialData];
        expectedResult = service.addProductAttributeToCollectionIfMissing(productAttributeCollection, productAttribute);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productAttribute);
      });

      it('should add only unique ProductAttribute to an array', () => {
        const productAttributeArray: IProductAttribute[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productAttributeCollection: IProductAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addProductAttributeToCollectionIfMissing(productAttributeCollection, ...productAttributeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productAttribute: IProductAttribute = sampleWithRequiredData;
        const productAttribute2: IProductAttribute = sampleWithPartialData;
        expectedResult = service.addProductAttributeToCollectionIfMissing([], productAttribute, productAttribute2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productAttribute);
        expect(expectedResult).toContain(productAttribute2);
      });

      it('should accept null and undefined values', () => {
        const productAttribute: IProductAttribute = sampleWithRequiredData;
        expectedResult = service.addProductAttributeToCollectionIfMissing([], null, productAttribute, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productAttribute);
      });

      it('should return initial array if no ProductAttribute is added', () => {
        const productAttributeCollection: IProductAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addProductAttributeToCollectionIfMissing(productAttributeCollection, undefined, null);
        expect(expectedResult).toEqual(productAttributeCollection);
      });
    });

    describe('compareProductAttribute', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductAttribute(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProductAttribute(entity1, entity2);
        const compareResult2 = service.compareProductAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProductAttribute(entity1, entity2);
        const compareResult2 = service.compareProductAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProductAttribute(entity1, entity2);
        const compareResult2 = service.compareProductAttribute(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
