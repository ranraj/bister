import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProjectReview } from '../project-review.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../project-review.test-samples';

import { ProjectReviewService } from './project-review.service';

const requireRestSample: IProjectReview = {
  ...sampleWithRequiredData,
};

describe('ProjectReview Service', () => {
  let service: ProjectReviewService;
  let httpMock: HttpTestingController;
  let expectedResult: IProjectReview | IProjectReview[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjectReviewService);
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

    it('should create a ProjectReview', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const projectReview = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(projectReview).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProjectReview', () => {
      const projectReview = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(projectReview).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProjectReview', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProjectReview', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProjectReview', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProjectReviewToCollectionIfMissing', () => {
      it('should add a ProjectReview to an empty array', () => {
        const projectReview: IProjectReview = sampleWithRequiredData;
        expectedResult = service.addProjectReviewToCollectionIfMissing([], projectReview);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectReview);
      });

      it('should not add a ProjectReview to an array that contains it', () => {
        const projectReview: IProjectReview = sampleWithRequiredData;
        const projectReviewCollection: IProjectReview[] = [
          {
            ...projectReview,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProjectReviewToCollectionIfMissing(projectReviewCollection, projectReview);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProjectReview to an array that doesn't contain it", () => {
        const projectReview: IProjectReview = sampleWithRequiredData;
        const projectReviewCollection: IProjectReview[] = [sampleWithPartialData];
        expectedResult = service.addProjectReviewToCollectionIfMissing(projectReviewCollection, projectReview);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectReview);
      });

      it('should add only unique ProjectReview to an array', () => {
        const projectReviewArray: IProjectReview[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const projectReviewCollection: IProjectReview[] = [sampleWithRequiredData];
        expectedResult = service.addProjectReviewToCollectionIfMissing(projectReviewCollection, ...projectReviewArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projectReview: IProjectReview = sampleWithRequiredData;
        const projectReview2: IProjectReview = sampleWithPartialData;
        expectedResult = service.addProjectReviewToCollectionIfMissing([], projectReview, projectReview2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectReview);
        expect(expectedResult).toContain(projectReview2);
      });

      it('should accept null and undefined values', () => {
        const projectReview: IProjectReview = sampleWithRequiredData;
        expectedResult = service.addProjectReviewToCollectionIfMissing([], null, projectReview, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectReview);
      });

      it('should return initial array if no ProjectReview is added', () => {
        const projectReviewCollection: IProjectReview[] = [sampleWithRequiredData];
        expectedResult = service.addProjectReviewToCollectionIfMissing(projectReviewCollection, undefined, null);
        expect(expectedResult).toEqual(projectReviewCollection);
      });
    });

    describe('compareProjectReview', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProjectReview(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProjectReview(entity1, entity2);
        const compareResult2 = service.compareProjectReview(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProjectReview(entity1, entity2);
        const compareResult2 = service.compareProjectReview(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProjectReview(entity1, entity2);
        const compareResult2 = service.compareProjectReview(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
