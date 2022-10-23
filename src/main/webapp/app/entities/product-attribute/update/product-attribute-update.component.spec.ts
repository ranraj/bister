import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductAttributeFormService } from './product-attribute-form.service';
import { ProductAttributeService } from '../service/product-attribute.service';
import { IProductAttribute } from '../product-attribute.model';

import { ProductAttributeUpdateComponent } from './product-attribute-update.component';

describe('ProductAttribute Management Update Component', () => {
  let comp: ProductAttributeUpdateComponent;
  let fixture: ComponentFixture<ProductAttributeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productAttributeFormService: ProductAttributeFormService;
  let productAttributeService: ProductAttributeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductAttributeUpdateComponent],
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
      .overrideTemplate(ProductAttributeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductAttributeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productAttributeFormService = TestBed.inject(ProductAttributeFormService);
    productAttributeService = TestBed.inject(ProductAttributeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const productAttribute: IProductAttribute = { id: 456 };

      activatedRoute.data = of({ productAttribute });
      comp.ngOnInit();

      expect(comp.productAttribute).toEqual(productAttribute);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttribute>>();
      const productAttribute = { id: 123 };
      jest.spyOn(productAttributeFormService, 'getProductAttribute').mockReturnValue(productAttribute);
      jest.spyOn(productAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productAttribute }));
      saveSubject.complete();

      // THEN
      expect(productAttributeFormService.getProductAttribute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productAttributeService.update).toHaveBeenCalledWith(expect.objectContaining(productAttribute));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttribute>>();
      const productAttribute = { id: 123 };
      jest.spyOn(productAttributeFormService, 'getProductAttribute').mockReturnValue({ id: null });
      jest.spyOn(productAttributeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttribute: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productAttribute }));
      saveSubject.complete();

      // THEN
      expect(productAttributeFormService.getProductAttribute).toHaveBeenCalled();
      expect(productAttributeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttribute>>();
      const productAttribute = { id: 123 };
      jest.spyOn(productAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productAttributeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
