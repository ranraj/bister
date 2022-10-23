import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPromotion } from '../promotion.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../promotion.test-samples';

import { PromotionService, RestPromotion } from './promotion.service';

const requireRestSample: RestPromotion = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  sendAt: sampleWithRequiredData.sendAt?.toJSON(),
};

describe('Promotion Service', () => {
  let service: PromotionService;
  let httpMock: HttpTestingController;
  let expectedResult: IPromotion | IPromotion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PromotionService);
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

    it('should create a Promotion', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const promotion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(promotion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Promotion', () => {
      const promotion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(promotion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Promotion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Promotion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Promotion', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPromotionToCollectionIfMissing', () => {
      it('should add a Promotion to an empty array', () => {
        const promotion: IPromotion = sampleWithRequiredData;
        expectedResult = service.addPromotionToCollectionIfMissing([], promotion);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(promotion);
      });

      it('should not add a Promotion to an array that contains it', () => {
        const promotion: IPromotion = sampleWithRequiredData;
        const promotionCollection: IPromotion[] = [
          {
            ...promotion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPromotionToCollectionIfMissing(promotionCollection, promotion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Promotion to an array that doesn't contain it", () => {
        const promotion: IPromotion = sampleWithRequiredData;
        const promotionCollection: IPromotion[] = [sampleWithPartialData];
        expectedResult = service.addPromotionToCollectionIfMissing(promotionCollection, promotion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(promotion);
      });

      it('should add only unique Promotion to an array', () => {
        const promotionArray: IPromotion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const promotionCollection: IPromotion[] = [sampleWithRequiredData];
        expectedResult = service.addPromotionToCollectionIfMissing(promotionCollection, ...promotionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const promotion: IPromotion = sampleWithRequiredData;
        const promotion2: IPromotion = sampleWithPartialData;
        expectedResult = service.addPromotionToCollectionIfMissing([], promotion, promotion2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(promotion);
        expect(expectedResult).toContain(promotion2);
      });

      it('should accept null and undefined values', () => {
        const promotion: IPromotion = sampleWithRequiredData;
        expectedResult = service.addPromotionToCollectionIfMissing([], null, promotion, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(promotion);
      });

      it('should return initial array if no Promotion is added', () => {
        const promotionCollection: IPromotion[] = [sampleWithRequiredData];
        expectedResult = service.addPromotionToCollectionIfMissing(promotionCollection, undefined, null);
        expect(expectedResult).toEqual(promotionCollection);
      });
    });

    describe('comparePromotion', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePromotion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePromotion(entity1, entity2);
        const compareResult2 = service.comparePromotion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePromotion(entity1, entity2);
        const compareResult2 = service.comparePromotion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePromotion(entity1, entity2);
        const compareResult2 = service.comparePromotion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
