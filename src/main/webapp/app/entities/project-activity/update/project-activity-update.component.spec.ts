import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjectActivityFormService } from './project-activity-form.service';
import { ProjectActivityService } from '../service/project-activity.service';
import { IProjectActivity } from '../project-activity.model';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { ProjectActivityUpdateComponent } from './project-activity-update.component';

describe('ProjectActivity Management Update Component', () => {
  let comp: ProjectActivityUpdateComponent;
  let fixture: ComponentFixture<ProjectActivityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectActivityFormService: ProjectActivityFormService;
  let projectActivityService: ProjectActivityService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjectActivityUpdateComponent],
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
      .overrideTemplate(ProjectActivityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectActivityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectActivityFormService = TestBed.inject(ProjectActivityFormService);
    projectActivityService = TestBed.inject(ProjectActivityService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Project query and add missing value', () => {
      const projectActivity: IProjectActivity = { id: 456 };
      const project: IProject = { id: 29837 };
      projectActivity.project = project;

      const projectCollection: IProject[] = [{ id: 70276 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectActivity });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projectActivity: IProjectActivity = { id: 456 };
      const project: IProject = { id: 52353 };
      projectActivity.project = project;

      activatedRoute.data = of({ projectActivity });
      comp.ngOnInit();

      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.projectActivity).toEqual(projectActivity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectActivity>>();
      const projectActivity = { id: 123 };
      jest.spyOn(projectActivityFormService, 'getProjectActivity').mockReturnValue(projectActivity);
      jest.spyOn(projectActivityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectActivity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectActivity }));
      saveSubject.complete();

      // THEN
      expect(projectActivityFormService.getProjectActivity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectActivityService.update).toHaveBeenCalledWith(expect.objectContaining(projectActivity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectActivity>>();
      const projectActivity = { id: 123 };
      jest.spyOn(projectActivityFormService, 'getProjectActivity').mockReturnValue({ id: null });
      jest.spyOn(projectActivityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectActivity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectActivity }));
      saveSubject.complete();

      // THEN
      expect(projectActivityFormService.getProjectActivity).toHaveBeenCalled();
      expect(projectActivityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectActivity>>();
      const projectActivity = { id: 123 };
      jest.spyOn(projectActivityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectActivity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectActivityService.update).toHaveBeenCalled();
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
