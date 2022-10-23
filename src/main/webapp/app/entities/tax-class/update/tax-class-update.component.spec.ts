import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TaxClassFormService } from './tax-class-form.service';
import { TaxClassService } from '../service/tax-class.service';
import { ITaxClass } from '../tax-class.model';

import { TaxClassUpdateComponent } from './tax-class-update.component';

describe('TaxClass Management Update Component', () => {
  let comp: TaxClassUpdateComponent;
  let fixture: ComponentFixture<TaxClassUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taxClassFormService: TaxClassFormService;
  let taxClassService: TaxClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TaxClassUpdateComponent],
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
      .overrideTemplate(TaxClassUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaxClassUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taxClassFormService = TestBed.inject(TaxClassFormService);
    taxClassService = TestBed.inject(TaxClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const taxClass: ITaxClass = { id: 456 };

      activatedRoute.data = of({ taxClass });
      comp.ngOnInit();

      expect(comp.taxClass).toEqual(taxClass);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxClass>>();
      const taxClass = { id: 123 };
      jest.spyOn(taxClassFormService, 'getTaxClass').mockReturnValue(taxClass);
      jest.spyOn(taxClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taxClass }));
      saveSubject.complete();

      // THEN
      expect(taxClassFormService.getTaxClass).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taxClassService.update).toHaveBeenCalledWith(expect.objectContaining(taxClass));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxClass>>();
      const taxClass = { id: 123 };
      jest.spyOn(taxClassFormService, 'getTaxClass').mockReturnValue({ id: null });
      jest.spyOn(taxClassService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxClass: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taxClass }));
      saveSubject.complete();

      // THEN
      expect(taxClassFormService.getTaxClass).toHaveBeenCalled();
      expect(taxClassService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxClass>>();
      const taxClass = { id: 123 };
      jest.spyOn(taxClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taxClassService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
