import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TaxRateFormService } from './tax-rate-form.service';
import { TaxRateService } from '../service/tax-rate.service';
import { ITaxRate } from '../tax-rate.model';
import { ITaxClass } from 'app/entities/tax-class/tax-class.model';
import { TaxClassService } from 'app/entities/tax-class/service/tax-class.service';

import { TaxRateUpdateComponent } from './tax-rate-update.component';

describe('TaxRate Management Update Component', () => {
  let comp: TaxRateUpdateComponent;
  let fixture: ComponentFixture<TaxRateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taxRateFormService: TaxRateFormService;
  let taxRateService: TaxRateService;
  let taxClassService: TaxClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TaxRateUpdateComponent],
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
      .overrideTemplate(TaxRateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaxRateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taxRateFormService = TestBed.inject(TaxRateFormService);
    taxRateService = TestBed.inject(TaxRateService);
    taxClassService = TestBed.inject(TaxClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TaxClass query and add missing value', () => {
      const taxRate: ITaxRate = { id: 456 };
      const taxClass: ITaxClass = { id: 26509 };
      taxRate.taxClass = taxClass;

      const taxClassCollection: ITaxClass[] = [{ id: 43247 }];
      jest.spyOn(taxClassService, 'query').mockReturnValue(of(new HttpResponse({ body: taxClassCollection })));
      const additionalTaxClasses = [taxClass];
      const expectedCollection: ITaxClass[] = [...additionalTaxClasses, ...taxClassCollection];
      jest.spyOn(taxClassService, 'addTaxClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taxRate });
      comp.ngOnInit();

      expect(taxClassService.query).toHaveBeenCalled();
      expect(taxClassService.addTaxClassToCollectionIfMissing).toHaveBeenCalledWith(
        taxClassCollection,
        ...additionalTaxClasses.map(expect.objectContaining)
      );
      expect(comp.taxClassesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const taxRate: ITaxRate = { id: 456 };
      const taxClass: ITaxClass = { id: 48936 };
      taxRate.taxClass = taxClass;

      activatedRoute.data = of({ taxRate });
      comp.ngOnInit();

      expect(comp.taxClassesSharedCollection).toContain(taxClass);
      expect(comp.taxRate).toEqual(taxRate);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxRate>>();
      const taxRate = { id: 123 };
      jest.spyOn(taxRateFormService, 'getTaxRate').mockReturnValue(taxRate);
      jest.spyOn(taxRateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxRate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taxRate }));
      saveSubject.complete();

      // THEN
      expect(taxRateFormService.getTaxRate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taxRateService.update).toHaveBeenCalledWith(expect.objectContaining(taxRate));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxRate>>();
      const taxRate = { id: 123 };
      jest.spyOn(taxRateFormService, 'getTaxRate').mockReturnValue({ id: null });
      jest.spyOn(taxRateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxRate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taxRate }));
      saveSubject.complete();

      // THEN
      expect(taxRateFormService.getTaxRate).toHaveBeenCalled();
      expect(taxRateService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaxRate>>();
      const taxRate = { id: 123 };
      jest.spyOn(taxRateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taxRate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taxRateService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTaxClass', () => {
      it('Should forward to taxClassService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(taxClassService, 'compareTaxClass');
        comp.compareTaxClass(entity, entity2);
        expect(taxClassService.compareTaxClass).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
