import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProjectSpecificationGroup } from '../project-specification-group.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../project-specification-group.test-samples';

import { ProjectSpecificationGroupService } from './project-specification-group.service';

const requireRestSample: IProjectSpecificationGroup = {
  ...sampleWithRequiredData,
};

describe('ProjectSpecificationGroup Service', () => {
  let service: ProjectSpecificationGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IProjectSpecificationGroup | IProjectSpecificationGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjectSpecificationGroupService);
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

    it('should create a ProjectSpecificationGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const projectSpecificationGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(projectSpecificationGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProjectSpecificationGroup', () => {
      const projectSpecificationGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(projectSpecificationGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProjectSpecificationGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProjectSpecificationGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProjectSpecificationGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProjectSpecificationGroupToCollectionIfMissing', () => {
      it('should add a ProjectSpecificationGroup to an empty array', () => {
        const projectSpecificationGroup: IProjectSpecificationGroup = sampleWithRequiredData;
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing([], projectSpecificationGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectSpecificationGroup);
      });

      it('should not add a ProjectSpecificationGroup to an array that contains it', () => {
        const projectSpecificationGroup: IProjectSpecificationGroup = sampleWithRequiredData;
        const projectSpecificationGroupCollection: IProjectSpecificationGroup[] = [
          {
            ...projectSpecificationGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing(
          projectSpecificationGroupCollection,
          projectSpecificationGroup
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProjectSpecificationGroup to an array that doesn't contain it", () => {
        const projectSpecificationGroup: IProjectSpecificationGroup = sampleWithRequiredData;
        const projectSpecificationGroupCollection: IProjectSpecificationGroup[] = [sampleWithPartialData];
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing(
          projectSpecificationGroupCollection,
          projectSpecificationGroup
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectSpecificationGroup);
      });

      it('should add only unique ProjectSpecificationGroup to an array', () => {
        const projectSpecificationGroupArray: IProjectSpecificationGroup[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const projectSpecificationGroupCollection: IProjectSpecificationGroup[] = [sampleWithRequiredData];
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing(
          projectSpecificationGroupCollection,
          ...projectSpecificationGroupArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projectSpecificationGroup: IProjectSpecificationGroup = sampleWithRequiredData;
        const projectSpecificationGroup2: IProjectSpecificationGroup = sampleWithPartialData;
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing(
          [],
          projectSpecificationGroup,
          projectSpecificationGroup2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projectSpecificationGroup);
        expect(expectedResult).toContain(projectSpecificationGroup2);
      });

      it('should accept null and undefined values', () => {
        const projectSpecificationGroup: IProjectSpecificationGroup = sampleWithRequiredData;
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing([], null, projectSpecificationGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projectSpecificationGroup);
      });

      it('should return initial array if no ProjectSpecificationGroup is added', () => {
        const projectSpecificationGroupCollection: IProjectSpecificationGroup[] = [sampleWithRequiredData];
        expectedResult = service.addProjectSpecificationGroupToCollectionIfMissing(projectSpecificationGroupCollection, undefined, null);
        expect(expectedResult).toEqual(projectSpecificationGroupCollection);
      });
    });

    describe('compareProjectSpecificationGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProjectSpecificationGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProjectSpecificationGroup(entity1, entity2);
        const compareResult2 = service.compareProjectSpecificationGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProjectSpecificationGroup(entity1, entity2);
        const compareResult2 = service.compareProjectSpecificationGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProjectSpecificationGroup(entity1, entity2);
        const compareResult2 = service.compareProjectSpecificationGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
