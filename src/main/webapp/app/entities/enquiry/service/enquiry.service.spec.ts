import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnquiry } from '../enquiry.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../enquiry.test-samples';

import { EnquiryService, RestEnquiry } from './enquiry.service';

const requireRestSample: RestEnquiry = {
  ...sampleWithRequiredData,
  raisedDate: sampleWithRequiredData.raisedDate?.toJSON(),
  lastResponseDate: sampleWithRequiredData.lastResponseDate?.toJSON(),
};

describe('Enquiry Service', () => {
  let service: EnquiryService;
  let httpMock: HttpTestingController;
  let expectedResult: IEnquiry | IEnquiry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnquiryService);
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

    it('should create a Enquiry', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const enquiry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(enquiry).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Enquiry', () => {
      const enquiry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(enquiry).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Enquiry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Enquiry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Enquiry', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEnquiryToCollectionIfMissing', () => {
      it('should add a Enquiry to an empty array', () => {
        const enquiry: IEnquiry = sampleWithRequiredData;
        expectedResult = service.addEnquiryToCollectionIfMissing([], enquiry);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enquiry);
      });

      it('should not add a Enquiry to an array that contains it', () => {
        const enquiry: IEnquiry = sampleWithRequiredData;
        const enquiryCollection: IEnquiry[] = [
          {
            ...enquiry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEnquiryToCollectionIfMissing(enquiryCollection, enquiry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Enquiry to an array that doesn't contain it", () => {
        const enquiry: IEnquiry = sampleWithRequiredData;
        const enquiryCollection: IEnquiry[] = [sampleWithPartialData];
        expectedResult = service.addEnquiryToCollectionIfMissing(enquiryCollection, enquiry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enquiry);
      });

      it('should add only unique Enquiry to an array', () => {
        const enquiryArray: IEnquiry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const enquiryCollection: IEnquiry[] = [sampleWithRequiredData];
        expectedResult = service.addEnquiryToCollectionIfMissing(enquiryCollection, ...enquiryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const enquiry: IEnquiry = sampleWithRequiredData;
        const enquiry2: IEnquiry = sampleWithPartialData;
        expectedResult = service.addEnquiryToCollectionIfMissing([], enquiry, enquiry2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enquiry);
        expect(expectedResult).toContain(enquiry2);
      });

      it('should accept null and undefined values', () => {
        const enquiry: IEnquiry = sampleWithRequiredData;
        expectedResult = service.addEnquiryToCollectionIfMissing([], null, enquiry, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enquiry);
      });

      it('should return initial array if no Enquiry is added', () => {
        const enquiryCollection: IEnquiry[] = [sampleWithRequiredData];
        expectedResult = service.addEnquiryToCollectionIfMissing(enquiryCollection, undefined, null);
        expect(expectedResult).toEqual(enquiryCollection);
      });
    });

    describe('compareEnquiry', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEnquiry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEnquiry(entity1, entity2);
        const compareResult2 = service.compareEnquiry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEnquiry(entity1, entity2);
        const compareResult2 = service.compareEnquiry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEnquiry(entity1, entity2);
        const compareResult2 = service.compareEnquiry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
