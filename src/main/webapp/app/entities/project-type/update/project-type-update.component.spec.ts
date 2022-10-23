import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjectTypeFormService } from './project-type-form.service';
import { ProjectTypeService } from '../service/project-type.service';
import { IProjectType } from '../project-type.model';

import { ProjectTypeUpdateComponent } from './project-type-update.component';

describe('ProjectType Management Update Component', () => {
  let comp: ProjectTypeUpdateComponent;
  let fixture: ComponentFixture<ProjectTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projectTypeFormService: ProjectTypeFormService;
  let projectTypeService: ProjectTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjectTypeUpdateComponent],
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
      .overrideTemplate(ProjectTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjectTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projectTypeFormService = TestBed.inject(ProjectTypeFormService);
    projectTypeService = TestBed.inject(ProjectTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const projectType: IProjectType = { id: 456 };

      activatedRoute.data = of({ projectType });
      comp.ngOnInit();

      expect(comp.projectType).toEqual(projectType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectType>>();
      const projectType = { id: 123 };
      jest.spyOn(projectTypeFormService, 'getProjectType').mockReturnValue(projectType);
      jest.spyOn(projectTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectType }));
      saveSubject.complete();

      // THEN
      expect(projectTypeFormService.getProjectType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(projectTypeService.update).toHaveBeenCalledWith(expect.objectContaining(projectType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectType>>();
      const projectType = { id: 123 };
      jest.spyOn(projectTypeFormService, 'getProjectType').mockReturnValue({ id: null });
      jest.spyOn(projectTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projectType }));
      saveSubject.complete();

      // THEN
      expect(projectTypeFormService.getProjectType).toHaveBeenCalled();
      expect(projectTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProjectType>>();
      const projectType = { id: 123 };
      jest.spyOn(projectTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projectType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projectTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
