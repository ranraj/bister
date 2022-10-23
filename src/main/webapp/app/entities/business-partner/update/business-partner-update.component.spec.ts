import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BusinessPartnerFormService } from './business-partner-form.service';
import { BusinessPartnerService } from '../service/business-partner.service';
import { IBusinessPartner } from '../business-partner.model';

import { BusinessPartnerUpdateComponent } from './business-partner-update.component';

describe('BusinessPartner Management Update Component', () => {
  let comp: BusinessPartnerUpdateComponent;
  let fixture: ComponentFixture<BusinessPartnerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let businessPartnerFormService: BusinessPartnerFormService;
  let businessPartnerService: BusinessPartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BusinessPartnerUpdateComponent],
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
      .overrideTemplate(BusinessPartnerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BusinessPartnerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    businessPartnerFormService = TestBed.inject(BusinessPartnerFormService);
    businessPartnerService = TestBed.inject(BusinessPartnerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const businessPartner: IBusinessPartner = { id: 456 };

      activatedRoute.data = of({ businessPartner });
      comp.ngOnInit();

      expect(comp.businessPartner).toEqual(businessPartner);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPartner>>();
      const businessPartner = { id: 123 };
      jest.spyOn(businessPartnerFormService, 'getBusinessPartner').mockReturnValue(businessPartner);
      jest.spyOn(businessPartnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPartner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businessPartner }));
      saveSubject.complete();

      // THEN
      expect(businessPartnerFormService.getBusinessPartner).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(businessPartnerService.update).toHaveBeenCalledWith(expect.objectContaining(businessPartner));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPartner>>();
      const businessPartner = { id: 123 };
      jest.spyOn(businessPartnerFormService, 'getBusinessPartner').mockReturnValue({ id: null });
      jest.spyOn(businessPartnerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPartner: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businessPartner }));
      saveSubject.complete();

      // THEN
      expect(businessPartnerFormService.getBusinessPartner).toHaveBeenCalled();
      expect(businessPartnerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinessPartner>>();
      const businessPartner = { id: 123 };
      jest.spyOn(businessPartnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businessPartner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(businessPartnerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
