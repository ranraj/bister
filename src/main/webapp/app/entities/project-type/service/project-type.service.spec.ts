import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProjectType } from '../project-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../project-type.test-samples';

import { ProjectTypeService } from './project-type.service';

const requireRestSample: IProjectType = {
  ...sampleWithRequiredData,
};

describe('ProjectType Service', () => {
  let service: ProjectTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IProjectType | IProjectType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjectTypeService);
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

    it('should create a ProjectType', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const projectType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(projectType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProjectType', () => {
      const projectType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(projectType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProjectType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProjectType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProjectType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProjectTypeToCollectionIfMissing', () => {
      it('should add a ProjectType to an empty array', () => {
        const projectType: IProjectType = sampleWithRequiredData;
        expectedResult = service.addProjectTypeToCollectionIfMissing([], projectType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectType);
      });

      it('should not add a ProjectType to an array that contains it', () => {
        const projectType: IProjectType = sampleWithRequiredData;
        const projectTypeCollection: IProjectType[] = [
          {
            ...projectType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProjectTypeToCollectionIfMissing(projectTypeCollection, projectType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProjectType to an array that doesn't contain it", () => {
        const projectType: IProjectType = sampleWithRequiredData;
        const projectTypeCollection: IProjectType[] = [sampleWithPartialData];
        expectedResult = service.addProjectTypeToCollectionIfMissing(projectTypeCollection, projectType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectType);
      });

      it('should add only unique ProjectType to an array', () => {
        const projectTypeArray: IProjectType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const projectTypeCollection: IProjectType[] = [sampleWithRequiredData];
        expectedResult = service.addProjectTypeToCollectionIfMissing(projectTypeCollection, ...projectTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projectType: IProjectType = sampleWithRequiredData;
        const projectType2: IProjectType = sampleWithPartialData;
        expectedResult = service.addProjectTypeToCollectionIfMissing([], projectType, projectType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectType);
        expect(expectedResult).toContain(projectType2);
      });

      it('should accept null and undefined values', () => {
        const projectType: IProjectType = sampleWithRequiredData;
        expectedResult = service.addProjectTypeToCollectionIfMissing([], null, projectType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectType);
      });

      it('should return initial array if no ProjectType is added', () => {
        const projectTypeCollection: IProjectType[] = [sampleWithRequiredData];
        expectedResult = service.addProjectTypeToCollectionIfMissing(projectTypeCollection, undefined, null);
        expect(expectedResult).toEqual(projectTypeCollection);
      });
    });

    describe('compareProjectType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProjectType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProjectType(entity1, entity2);
        const compareResult2 = service.compareProjectType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProjectType(entity1, entity2);
        const compareResult2 = service.compareProjectType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProjectType(entity1, entity2);
        const compareResult2 = service.compareProjectType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
