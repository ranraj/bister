import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICertification } from '../certification.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../certification.test-samples';

import { CertificationService, RestCertification } from './certification.service';

const requireRestSample: RestCertification = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('Certification Service', () => {
  let service: CertificationService;
  let httpMock: HttpTestingController;
  let expectedResult: ICertification | ICertification[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CertificationService);
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

    it('should create a Certification', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const certification = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(certification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Certification', () => {
      const certification = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(certification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Certification', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Certification', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Certification', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCertificationToCollectionIfMissing', () => {
      it('should add a Certification to an empty array', () => {
        const certification: ICertification = sampleWithRequiredData;
        expectedResult = service.addCertificationToCollectionIfMissing([], certification);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certification);
      });

      it('should not add a Certification to an array that contains it', () => {
        const certification: ICertification = sampleWithRequiredData;
        const certificationCollection: ICertification[] = [
          {
            ...certification,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCertificationToCollectionIfMissing(certificationCollection, certification);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Certification to an array that doesn't contain it", () => {
        const certification: ICertification = sampleWithRequiredData;
        const certificationCollection: ICertification[] = [sampleWithPartialData];
        expectedResult = service.addCertificationToCollectionIfMissing(certificationCollection, certification);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certification);
      });

      it('should add only unique Certification to an array', () => {
        const certificationArray: ICertification[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const certificationCollection: ICertification[] = [sampleWithRequiredData];
        expectedResult = service.addCertificationToCollectionIfMissing(certificationCollection, ...certificationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const certification: ICertification = sampleWithRequiredData;
        const certification2: ICertification = sampleWithPartialData;
        expectedResult = service.addCertificationToCollectionIfMissing([], certification, certification2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certification);
        expect(expectedResult).toContain(certification2);
      });

      it('should accept null and undefined values', () => {
        const certification: ICertification = sampleWithRequiredData;
        expectedResult = service.addCertificationToCollectionIfMissing([], null, certification, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certification);
      });

      it('should return initial array if no Certification is added', () => {
        const certificationCollection: ICertification[] = [sampleWithRequiredData];
        expectedResult = service.addCertificationToCollectionIfMissing(certificationCollection, undefined, null);
        expect(expectedResult).toEqual(certificationCollection);
      });
    });

    describe('compareCertification', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCertification(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCertification(entity1, entity2);
        const compareResult2 = service.compareCertification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCertification(entity1, entity2);
        const compareResult2 = service.compareCertification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCertification(entity1, entity2);
        const compareResult2 = service.compareCertification(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
