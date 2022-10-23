import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductVariationFormService } from './product-variation-form.service';
import { ProductVariationService } from '../service/product-variation.service';
import { IProductVariation } from '../product-variation.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductVariationUpdateComponent } from './product-variation-update.component';

describe('ProductVariation Management Update Component', () => {
  let comp: ProductVariationUpdateComponent;
  let fixture: ComponentFixture<ProductVariationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productVariationFormService: ProductVariationFormService;
  let productVariationService: ProductVariationService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductVariationUpdateComponent],
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
      .overrideTemplate(ProductVariationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductVariationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productVariationFormService = TestBed.inject(ProductVariationFormService);
    productVariationService = TestBed.inject(ProductVariationService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const productVariation: IProductVariation = { id: 456 };
      const product: IProduct = { id: 60113 };
      productVariation.product = product;

      const productCollection: IProduct[] = [{ id: 50437 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productVariation });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productVariation: IProductVariation = { id: 456 };
      const product: IProduct = { id: 9740 };
      productVariation.product = product;

      activatedRoute.data = of({ productVariation });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productVariation).toEqual(productVariation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariation>>();
      const productVariation = { id: 123 };
      jest.spyOn(productVariationFormService, 'getProductVariation').mockReturnValue(productVariation);
      jest.spyOn(productVariationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productVariation }));
      saveSubject.complete();

      // THEN
      expect(productVariationFormService.getProductVariation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productVariationService.update).toHaveBeenCalledWith(expect.objectContaining(productVariation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariation>>();
      const productVariation = { id: 123 };
      jest.spyOn(productVariationFormService, 'getProductVariation').mockReturnValue({ id: null });
      jest.spyOn(productVariationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productVariation }));
      saveSubject.complete();

      // THEN
      expect(productVariationFormService.getProductVariation).toHaveBeenCalled();
      expect(productVariationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductVariation>>();
      const productVariation = { id: 123 };
      jest.spyOn(productVariationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productVariation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productVariationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduct', () => {
      it('Should forward to productService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
