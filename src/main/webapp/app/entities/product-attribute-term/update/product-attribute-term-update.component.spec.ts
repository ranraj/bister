import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductAttributeTermFormService } from './product-attribute-term-form.service';
import { ProductAttributeTermService } from '../service/product-attribute-term.service';
import { IProductAttributeTerm } from '../product-attribute-term.model';
import { IProductAttribute } from 'app/entities/product-attribute/product-attribute.model';
import { ProductAttributeService } from 'app/entities/product-attribute/service/product-attribute.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductAttributeTermUpdateComponent } from './product-attribute-term-update.component';

describe('ProductAttributeTerm Management Update Component', () => {
  let comp: ProductAttributeTermUpdateComponent;
  let fixture: ComponentFixture<ProductAttributeTermUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productAttributeTermFormService: ProductAttributeTermFormService;
  let productAttributeTermService: ProductAttributeTermService;
  let productAttributeService: ProductAttributeService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductAttributeTermUpdateComponent],
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
      .overrideTemplate(ProductAttributeTermUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductAttributeTermUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productAttributeTermFormService = TestBed.inject(ProductAttributeTermFormService);
    productAttributeTermService = TestBed.inject(ProductAttributeTermService);
    productAttributeService = TestBed.inject(ProductAttributeService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProductAttribute query and add missing value', () => {
      const productAttributeTerm: IProductAttributeTerm = { id: 456 };
      const productAttribute: IProductAttribute = { id: 30113 };
      productAttributeTerm.productAttribute = productAttribute;

      const productAttributeCollection: IProductAttribute[] = [{ id: 91508 }];
      jest.spyOn(productAttributeService, 'query').mockReturnValue(of(new HttpResponse({ body: productAttributeCollection })));
      const additionalProductAttributes = [productAttribute];
      const expectedCollection: IProductAttribute[] = [...additionalProductAttributes, ...productAttributeCollection];
      jest.spyOn(productAttributeService, 'addProductAttributeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productAttributeTerm });
      comp.ngOnInit();

      expect(productAttributeService.query).toHaveBeenCalled();
      expect(productAttributeService.addProductAttributeToCollectionIfMissing).toHaveBeenCalledWith(
        productAttributeCollection,
        ...additionalProductAttributes.map(expect.objectContaining)
      );
      expect(comp.productAttributesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const productAttributeTerm: IProductAttributeTerm = { id: 456 };
      const product: IProduct = { id: 12428 };
      productAttributeTerm.product = product;

      const productCollection: IProduct[] = [{ id: 38332 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productAttributeTerm });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productAttributeTerm: IProductAttributeTerm = { id: 456 };
      const productAttribute: IProductAttribute = { id: 77639 };
      productAttributeTerm.productAttribute = productAttribute;
      const product: IProduct = { id: 13586 };
      productAttributeTerm.product = product;

      activatedRoute.data = of({ productAttributeTerm });
      comp.ngOnInit();

      expect(comp.productAttributesSharedCollection).toContain(productAttribute);
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productAttributeTerm).toEqual(productAttributeTerm);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttributeTerm>>();
      const productAttributeTerm = { id: 123 };
      jest.spyOn(productAttributeTermFormService, 'getProductAttributeTerm').mockReturnValue(productAttributeTerm);
      jest.spyOn(productAttributeTermService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttributeTerm });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productAttributeTerm }));
      saveSubject.complete();

      // THEN
      expect(productAttributeTermFormService.getProductAttributeTerm).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productAttributeTermService.update).toHaveBeenCalledWith(expect.objectContaining(productAttributeTerm));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttributeTerm>>();
      const productAttributeTerm = { id: 123 };
      jest.spyOn(productAttributeTermFormService, 'getProductAttributeTerm').mockReturnValue({ id: null });
      jest.spyOn(productAttributeTermService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttributeTerm: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productAttributeTerm }));
      saveSubject.complete();

      // THEN
      expect(productAttributeTermFormService.getProductAttributeTerm).toHaveBeenCalled();
      expect(productAttributeTermService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductAttributeTerm>>();
      const productAttributeTerm = { id: 123 };
      jest.spyOn(productAttributeTermService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productAttributeTerm });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productAttributeTermService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProductAttribute', () => {
      it('Should forward to productAttributeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productAttributeService, 'compareProductAttribute');
        comp.compareProductAttribute(entity, entity2);
        expect(productAttributeService.compareProductAttribute).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
