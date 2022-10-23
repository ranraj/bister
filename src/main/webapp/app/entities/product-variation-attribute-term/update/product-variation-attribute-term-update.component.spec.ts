import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductVariationAttributeTermFormService } from './product-variation-attribute-term-form.service';
import { ProductVariationAttributeTermService } from '../service/product-variation-attribute-term.service';
import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';

import { ProductVariationAttributeTermUpdateComponent } from './product-variation-attribute-term-update.component';

describe('ProductVariationAttributeTerm Management Update Component', () => {
  let comp: ProductVariationAttributeTermUpdateComponent;
  let fixture: ComponentFixture<ProductVariationAttributeTermUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productVariationAttributeTermFormService: ProductVariationAttributeTermFormService;
  let productVariationAttributeTermService: ProductVariationAttributeTermService;
  let productVariationService: ProductVariationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductVariationAttributeTermUpdateComponent],
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
      .overrideTemplate(ProductVariationAttributeTermUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductVariationAttributeTermUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productVariationAttributeTermFormService = TestBed.inject(ProductVariationAttributeTermFormService);
    productVariationAttributeTermService = TestBed.inject(ProductVariationAttributeTermService);
    productVariationService = TestBed.inject(ProductVariationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProductVariation query and add missing value', () => {
      const productVariationAttributeTerm: IProductVariationAttributeTerm = { id: 456 };
      const productVariation: IProductVariation = { id: 44693 };
      productVariationAttributeTerm.productVariation = productVariation;

      const productVariationCollection: IProductVariation[] = [{ id: 73383 }];
      jest.spyOn(productVariationService, 'query').mockReturnValue(of(new HttpResponse({ body: productVariationCollection })));
      const additionalProductVariations = [productVariation];
      const expectedCollection: IProductVariation[] = [...additionalProductVariations, ...productVariationCollection];
      jest.spyOn(productVariationService, 'addProductVariationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productVariationAttributeTerm });
      comp.ngOnInit();

      expect(productVariationService.query).toHaveBeenCalled();
      expect(productVariationService.addProductVariationToCollectionIfMissing).toHaveBeenCalledWith(
        productVariationCollection,
        ...additionalProductVariations.map(expect.objectContaining)
      );
      expect(comp.productVariationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productVariationAttributeTerm: IProductVariationAttributeTerm = { id: 456 };
      const productVariation: IProductVariation = { id: 61243 };
      productVariationAttributeTerm.productVariation = productVariation;

      activatedRoute.data = of({ productVariationAttributeTerm });
      comp.ngOnInit();

      expect(comp.productVariationsSharedCollection).toContain(productVariation);
      expect(comp.productVariationAttributeTerm).toEqual(productVariationAttributeTerm);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariationAttributeTerm>>();
      const productVariationAttributeTerm = { id: 123 };
      jest
        .spyOn(productVariationAttributeTermFormService, 'getProductVariationAttributeTerm')
        .mockReturnValue(productVariationAttributeTerm);
      jest.spyOn(productVariationAttributeTermService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariationAttributeTerm });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productVariationAttributeTerm }));
      saveSubject.complete();

      // THEN
      expect(productVariationAttributeTermFormService.getProductVariationAttributeTerm).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productVariationAttributeTermService.update).toHaveBeenCalledWith(expect.objectContaining(productVariationAttributeTerm));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariationAttributeTerm>>();
      const productVariationAttributeTerm = { id: 123 };
      jest.spyOn(productVariationAttributeTermFormService, 'getProductVariationAttributeTerm').mockReturnValue({ id: null });
      jest.spyOn(productVariationAttributeTermService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariationAttributeTerm: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productVariationAttributeTerm }));
      saveSubject.complete();

      // THEN
      expect(productVariationAttributeTermFormService.getProductVariationAttributeTerm).toHaveBeenCalled();
      expect(productVariationAttributeTermService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariationAttributeTerm>>();
      const productVariationAttributeTerm = { id: 123 };
      jest.spyOn(productVariationAttributeTermService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariationAttributeTerm });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productVariationAttributeTermService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProductVariation', () => {
      it('Should forward to productVariationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productVariationService, 'compareProductVariation');
        comp.compareProductVariation(entity, entity2);
        expect(productVariationService.compareProductVariation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
