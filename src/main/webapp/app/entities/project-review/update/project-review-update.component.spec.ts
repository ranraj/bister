import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjectReviewFormService } from './project-review-form.service';
import { ProjectReviewService } from '../service/project-review.service';
import { IProjectReview } from '../project-review.model';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { ProjectReviewUpdateComponent } from './project-review-update.component';

describe('ProjectReview Management Update Component', () => {
  let comp: ProjectReviewUpdateComponent;
  let fixture: ComponentFixture<ProjectReviewUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectReviewFormService: ProjectReviewFormService;
  let projectReviewService: ProjectReviewService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjectReviewUpdateComponent],
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
      .overrideTemplate(ProjectReviewUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectReviewUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectReviewFormService = TestBed.inject(ProjectReviewFormService);
    projectReviewService = TestBed.inject(ProjectReviewService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Project query and add missing value', () => {
      const projectReview: IProjectReview = { id: 456 };
      const project: IProject = { id: 93555 };
      projectReview.project = project;

      const projectCollection: IProject[] = [{ id: 93982 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projectReview });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projectReview: IProjectReview = { id: 456 };
      const project: IProject = { id: 76186 };
      projectReview.project = project;

      activatedRoute.data = of({ projectReview });
      comp.ngOnInit();

      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.projectReview).toEqual(projectReview);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectReview>>();
      const projectReview = { id: 123 };
      jest.spyOn(projectReviewFormService, 'getProjectReview').mockReturnValue(projectReview);
      jest.spyOn(projectReviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectReview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectReview }));
      saveSubject.complete();

      // THEN
      expect(projectReviewFormService.getProjectReview).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectReviewService.update).toHaveBeenCalledWith(expect.objectContaining(projectReview));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectReview>>();
      const projectReview = { id: 123 };
      jest.spyOn(projectReviewFormService, 'getProjectReview').mockReturnValue({ id: null });
      jest.spyOn(projectReviewService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectReview: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectReview }));
      saveSubject.complete();

      // THEN
      expect(projectReviewFormService.getProjectReview).toHaveBeenCalled();
      expect(projectReviewService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectReview>>();
      const projectReview = { id: 123 };
      jest.spyOn(projectReviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectReview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectReviewService.update).toHaveBeenCalled();
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
