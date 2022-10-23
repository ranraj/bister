import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnquiryResponse } from '../enquiry-response.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../enquiry-response.test-samples';

import { EnquiryResponseService } from './enquiry-response.service';

const requireRestSample: IEnquiryResponse = {
  ...sampleWithRequiredData,
};

describe('EnquiryResponse Service', () => {
  let service: EnquiryResponseService;
  let httpMock: HttpTestingController;
  let expectedResult: IEnquiryResponse | IEnquiryResponse[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnquiryResponseService);
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

    it('should create a EnquiryResponse', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const enquiryResponse = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(enquiryResponse).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EnquiryResponse', () => {
      const enquiryResponse = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(enquiryResponse).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EnquiryResponse', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EnquiryResponse', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EnquiryResponse', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEnquiryResponseToCollectionIfMissing', () => {
      it('should add a EnquiryResponse to an empty array', () => {
        const enquiryResponse: IEnquiryResponse = sampleWithRequiredData;
        expectedResult = service.addEnquiryResponseToCollectionIfMissing([], enquiryResponse);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enquiryResponse);
      });

      it('should not add a EnquiryResponse to an array that contains it', () => {
        const enquiryResponse: IEnquiryResponse = sampleWithRequiredData;
        const enquiryResponseCollection: IEnquiryResponse[] = [
          {
            ...enquiryResponse,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEnquiryResponseToCollectionIfMissing(enquiryResponseCollection, enquiryResponse);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EnquiryResponse to an array that doesn't contain it", () => {
        const enquiryResponse: IEnquiryResponse = sampleWithRequiredData;
        const enquiryResponseCollection: IEnquiryResponse[] = [sampleWithPartialData];
        expectedResult = service.addEnquiryResponseToCollectionIfMissing(enquiryResponseCollection, enquiryResponse);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enquiryResponse);
      });

      it('should add only unique EnquiryResponse to an array', () => {
        const enquiryResponseArray: IEnquiryResponse[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const enquiryResponseCollection: IEnquiryResponse[] = [sampleWithRequiredData];
        expectedResult = service.addEnquiryResponseToCollectionIfMissing(enquiryResponseCollection, ...enquiryResponseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const enquiryResponse: IEnquiryResponse = sampleWithRequiredData;
        const enquiryResponse2: IEnquiryResponse = sampleWithPartialData;
        expectedResult = service.addEnquiryResponseToCollectionIfMissing([], enquiryResponse, enquiryResponse2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(enquiryResponse);
        expect(expectedResult).toContain(enquiryResponse2);
      });

      it('should accept null and undefined values', () => {
        const enquiryResponse: IEnquiryResponse = sampleWithRequiredData;
        expectedResult = service.addEnquiryResponseToCollectionIfMissing([], null, enquiryResponse, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(enquiryResponse);
      });

      it('should return initial array if no EnquiryResponse is added', () => {
        const enquiryResponseCollection: IEnquiryResponse[] = [sampleWithRequiredData];
        expectedResult = service.addEnquiryResponseToCollectionIfMissing(enquiryResponseCollection, undefined, null);
        expect(expectedResult).toEqual(enquiryResponseCollection);
      });
    });

    describe('compareEnquiryResponse', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEnquiryResponse(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEnquiryResponse(entity1, entity2);
        const compareResult2 = service.compareEnquiryResponse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEnquiryResponse(entity1, entity2);
        const compareResult2 = service.compareEnquiryResponse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEnquiryResponse(entity1, entity2);
        const compareResult2 = service.compareEnquiryResponse(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
