import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjectSpecificationGroupFormService } from './project-specification-group-form.service';
import { ProjectSpecificationGroupService } from '../service/project-specification-group.service';
import { IProjectSpecificationGroup } from '../project-specification-group.model';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { ProjectSpecificationGroupUpdateComponent } from './project-specification-group-update.component';

describe('ProjectSpecificationGroup Management Update Component', () => {
  let comp: ProjectSpecificationGroupUpdateComponent;
  let fixture: ComponentFixture<ProjectSpecificationGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectSpecificationGroupFormService: ProjectSpecificationGroupFormService;
  let projectSpecificationGroupService: ProjectSpecificationGroupService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjectSpecificationGroupUpdateComponent],
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
      .overrideTemplate(ProjectSpecificationGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectSpecificationGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectSpecificationGroupFormService = TestBed.inject(ProjectSpecificationGroupFormService);
    projectSpecificationGroupService = TestBed.inject(ProjectSpecificationGroupService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Project query and add missing value', () => {
      const projectSpecificationGroup: IProjectSpecificationGroup = { id: 456 };
      const project: IProject = { id: 36169 };
      projectSpecificationGroup.project = project;

      const projectCollection: IProject[] = [{ id: 10081 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectSpecificationGroup });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projectSpecificationGroup: IProjectSpecificationGroup = { id: 456 };
      const project: IProject = { id: 82889 };
      projectSpecificationGroup.project = project;

      activatedRoute.data = of({ projectSpecificationGroup });
      comp.ngOnInit();

      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.projectSpecificationGroup).toEqual(projectSpecificationGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecificationGroup>>();
      const projectSpecificationGroup = { id: 123 };
      jest.spyOn(projectSpecificationGroupFormService, 'getProjectSpecificationGroup').mockReturnValue(projectSpecificationGroup);
      jest.spyOn(projectSpecificationGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecificationGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectSpecificationGroup }));
      saveSubject.complete();

      // THEN
      expect(projectSpecificationGroupFormService.getProjectSpecificationGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectSpecificationGroupService.update).toHaveBeenCalledWith(expect.objectContaining(projectSpecificationGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecificationGroup>>();
      const projectSpecificationGroup = { id: 123 };
      jest.spyOn(projectSpecificationGroupFormService, 'getProjectSpecificationGroup').mockReturnValue({ id: null });
      jest.spyOn(projectSpecificationGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecificationGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectSpecificationGroup }));
      saveSubject.complete();

      // THEN
      expect(projectSpecificationGroupFormService.getProjectSpecificationGroup).toHaveBeenCalled();
      expect(projectSpecificationGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectSpecificationGroup>>();
      const projectSpecificationGroup = { id: 123 };
      jest.spyOn(projectSpecificationGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectSpecificationGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectSpecificationGroupService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
