import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjectSpecificationFormService } from './project-specification-form.service';
import { ProjectSpecificationService } from '../service/project-specification.service';
import { IProjectSpecification } from '../project-specification.model';
import { IProjectSpecificationGroup } from 'app/entities/project-specification-group/project-specification-group.model';
import { ProjectSpecificationGroupService } from 'app/entities/project-specification-group/service/project-specification-group.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { ProjectSpecificationUpdateComponent } from './project-specification-update.component';

describe('ProjectSpecification Management Update Component', () => {
  let comp: ProjectSpecificationUpdateComponent;
  let fixture: ComponentFixture<ProjectSpecificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectSpecificationFormService: ProjectSpecificationFormService;
  let projectSpecificationService: ProjectSpecificationService;
  let projectSpecificationGroupService: ProjectSpecificationGroupService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjectSpecificationUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProjectSpecificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectSpecificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectSpecificationFormService = TestBed.inject(ProjectSpecificationFormService);
    projectSpecificationService = TestBed.inject(ProjectSpecificationService);
    projectSpecificationGroupService = TestBed.inject(ProjectSpecificationGroupService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProjectSpecificationGroup query and add missing value', () => {
      const projectSpecification: IProjectSpecification = { id: 456 };
      const projectSpecificationGroup: IProjectSpecificationGroup = { id: 81694 };
      projectSpecification.projectSpecificationGroup = projectSpecificationGroup;

      const projectSpecificationGroupCollection: IProjectSpecificationGroup[] = [{ id: 11181 }];
      jest
        .spyOn(projectSpecificationGroupService, 'query')
        .mockReturnValue(of(new HttpResponse({ body: projectSpecificationGroupCollection })));
      const additionalProjectSpecificationGroups = [projectSpecificationGroup];
      const expectedCollection: IProjectSpecificationGroup[] = [
        ...additionalProjectSpecificationGroups,
        ...projectSpecificationGroupCollection,
      ];
      jest.spyOn(projectSpecificationGroupService, 'addProjectSpecificationGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectSpecification });
      comp.ngOnInit();

      expect(projectSpecificationGroupService.query).toHaveBeenCalled();
      expect(projectSpecificationGroupService.addProjectSpecificationGroupToCollectionIfMissing).toHaveBeenCalledWith(
        projectSpecificationGroupCollection,
        ...additionalProjectSpecificationGroups.map(expect.objectContaining)
      );
      expect(comp.projectSpecificationGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Project query and add missing value', () => {
      const projectSpecification: IProjectSpecification = { id: 456 };
      const project: IProject = { id: 38737 };
      projectSpecification.project = project;

      const projectCollection: IProject[] = [{ id: 10760 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectSpecification });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projectSpecification: IProjectSpecification = { id: 456 };
      const projectSpecificationGroup: IProjectSpecificationGroup = { id: 22724 };
      projectSpecification.projectSpecificationGroup = projectSpecificationGroup;
      const project: IProject = { id: 57380 };
      projectSpecification.project = project;

      activatedRoute.data = of({ projectSpecification });
      comp.ngOnInit();

      expect(comp.projectSpecificationGroupsSharedCollection).toContain(projectSpecificationGroup);
      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.projectSpecification).toEqual(projectSpecification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecification>>();
      const projectSpecification = { id: 123 };
      jest.spyOn(projectSpecificationFormService, 'getProjectSpecification').mockReturnValue(projectSpecification);
      jest.spyOn(projectSpecificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectSpecification }));
      saveSubject.complete();

      // THEN
      expect(projectSpecificationFormService.getProjectSpecification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectSpecificationService.update).toHaveBeenCalledWith(expect.objectContaining(projectSpecification));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecification>>();
      const projectSpecification = { id: 123 };
      jest.spyOn(projectSpecificationFormService, 'getProjectSpecification').mockReturnValue({ id: null });
      jest.spyOn(projectSpecificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectSpecification }));
      saveSubject.complete();

      // THEN
      expect(projectSpecificationFormService.getProjectSpecification).toHaveBeenCalled();
      expect(projectSpecificationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecification>>();
      const projectSpecification = { id: 123 };
      jest.spyOn(projectSpecificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectSpecificationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProjectSpecificationGroup', () => {
      it('Should forward to projectSpecificationGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(projectSpecificationGroupService, 'compareProjectSpecificationGroup');
        comp.compareProjectSpecificationGroup(entity, entity2);
        expect(projectSpecificationGroupService.compareProjectSpecificationGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProject', () => {
      it('Should forward to projectService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(projectService, 'compareProject');
        comp.compareProject(entity, entity2);
        expect(projectService.compareProject).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
