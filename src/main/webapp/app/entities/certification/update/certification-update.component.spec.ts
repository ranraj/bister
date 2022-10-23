import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CertificationFormService } from './certification-form.service';
import { CertificationService } from '../service/certification.service';
import { ICertification } from '../certification.model';

import { CertificationUpdateComponent } from './certification-update.component';

describe('Certification Management Update Component', () => {
  let comp: CertificationUpdateComponent;
  let fixture: ComponentFixture<CertificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let certificationFormService: CertificationFormService;
  let certificationService: CertificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CertificationUpdateComponent],
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
      .overrideTemplate(CertificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CertificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    certificationFormService = TestBed.inject(CertificationFormService);
    certificationService = TestBed.inject(CertificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const certification: ICertification = { id: 456 };

      activatedRoute.data = of({ certification });
      comp.ngOnInit();

      expect(comp.certification).toEqual(certification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertification>>();
      const certification = { id: 123 };
      jest.spyOn(certificationFormService, 'getCertification').mockReturnValue(certification);
      jest.spyOn(certificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certification }));
      saveSubject.complete();

      // THEN
      expect(certificationFormService.getCertification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(certificationService.update).toHaveBeenCalledWith(expect.objectContaining(certification));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertification>>();
      const certification = { id: 123 };
      jest.spyOn(certificationFormService, 'getCertification').mockReturnValue({ id: null });
      jest.spyOn(certificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certification }));
      saveSubject.complete();

      // THEN
      expect(certificationFormService.getCertification).toHaveBeenCalled();
      expect(certificationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertification>>();
      const certification = { id: 123 };
      jest.spyOn(certificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(certificationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
