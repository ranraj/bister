import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProjectSpecification } from '../project-specification.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../project-specification.test-samples';

import { ProjectSpecificationService } from './project-specification.service';

const requireRestSample: IProjectSpecification = {
  ...sampleWithRequiredData,
};

describe('ProjectSpecification Service', () => {
  let service: ProjectSpecificationService;
  let httpMock: HttpTestingController;
  let expectedResult: IProjectSpecification | IProjectSpecification[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjectSpecificationService);
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

    it('should create a ProjectSpecification', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const projectSpecification = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(projectSpecification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProjectSpecification', () => {
      const projectSpecification = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(projectSpecification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProjectSpecification', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProjectSpecification', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProjectSpecification', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProjectSpecificationToCollectionIfMissing', () => {
      it('should add a ProjectSpecification to an empty array', () => {
        const projectSpecification: IProjectSpecification = sampleWithRequiredData;
        expectedResult = service.addProjectSpecificationToCollectionIfMissing([], projectSpecification);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectSpecification);
      });

      it('should not add a ProjectSpecification to an array that contains it', () => {
        const projectSpecification: IProjectSpecification = sampleWithRequiredData;
        const projectSpecificationCollection: IProjectSpecification[] = [
          {
            ...projectSpecification,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProjectSpecificationToCollectionIfMissing(projectSpecificationCollection, projectSpecification);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProjectSpecification to an array that doesn't contain it", () => {
        const projectSpecification: IProjectSpecification = sampleWithRequiredData;
        const projectSpecificationCollection: IProjectSpecification[] = [sampleWithPartialData];
        expectedResult = service.addProjectSpecificationToCollectionIfMissing(projectSpecificationCollection, projectSpecification);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectSpecification);
      });

      it('should add only unique ProjectSpecification to an array', () => {
        const projectSpecificationArray: IProjectSpecification[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const projectSpecificationCollection: IProjectSpecification[] = [sampleWithRequiredData];
        expectedResult = service.addProjectSpecificationToCollectionIfMissing(projectSpecificationCollection, ...projectSpecificationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projectSpecification: IProjectSpecification = sampleWithRequiredData;
        const projectSpecification2: IProjectSpecification = sampleWithPartialData;
        expectedResult = service.addProjectSpecificationToCollectionIfMissing([], projectSpecification, projectSpecification2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectSpecification);
        expect(expectedResult).toContain(projectSpecification2);
      });

      it('should accept null and undefined values', () => {
        const projectSpecification: IProjectSpecification = sampleWithRequiredData;
        expectedResult = service.addProjectSpecificationToCollectionIfMissing([], null, projectSpecification, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectSpecification);
      });

      it('should return initial array if no ProjectSpecification is added', () => {
        const projectSpecificationCollection: IProjectSpecification[] = [sampleWithRequiredData];
        expectedResult = service.addProjectSpecificationToCollectionIfMissing(projectSpecificationCollection, undefined, null);
        expect(expectedResult).toEqual(projectSpecificationCollection);
      });
    });

    describe('compareProjectSpecification', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProjectSpecification(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProjectSpecification(entity1, entity2);
        const compareResult2 = service.compareProjectSpecification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProjectSpecification(entity1, entity2);
        const compareResult2 = service.compareProjectSpecification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProjectSpecification(entity1, entity2);
        const compareResult2 = service.compareProjectSpecification(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
