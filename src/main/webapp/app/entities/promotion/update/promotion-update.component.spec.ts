import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PromotionFormService } from './promotion-form.service';
import { PromotionService } from '../service/promotion.service';
import { IPromotion } from '../promotion.model';

import { PromotionUpdateComponent } from './promotion-update.component';

describe('Promotion Management Update Component', () => {
  let comp: PromotionUpdateComponent;
  let fixture: ComponentFixture<PromotionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let promotionFormService: PromotionFormService;
  let promotionService: PromotionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PromotionUpdateComponent],
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
      .overrideTemplate(PromotionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PromotionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    promotionFormService = TestBed.inject(PromotionFormService);
    promotionService = TestBed.inject(PromotionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const promotion: IPromotion = { id: 456 };

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(comp.promotion).toEqual(promotion);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 123 };
      jest.spyOn(promotionFormService, 'getPromotion').mockReturnValue(promotion);
      jest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: promotion }));
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(promotionService.update).toHaveBeenCalledWith(expect.objectContaining(promotion));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 123 };
      jest.spyOn(promotionFormService, 'getPromotion').mockReturnValue({ id: null });
      jest.spyOn(promotionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: promotion }));
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(promotionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 123 };
      jest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(promotionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
