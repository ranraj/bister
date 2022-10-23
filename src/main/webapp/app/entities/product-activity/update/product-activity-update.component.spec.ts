import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductActivityFormService } from './product-activity-form.service';
import { ProductActivityService } from '../service/product-activity.service';
import { IProductActivity } from '../product-activity.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductActivityUpdateComponent } from './product-activity-update.component';

describe('ProductActivity Management Update Component', () => {
  let comp: ProductActivityUpdateComponent;
  let fixture: ComponentFixture<ProductActivityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productActivityFormService: ProductActivityFormService;
  let productActivityService: ProductActivityService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductActivityUpdateComponent],
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
      .overrideTemplate(ProductActivityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductActivityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productActivityFormService = TestBed.inject(ProductActivityFormService);
    productActivityService = TestBed.inject(ProductActivityService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const productActivity: IProductActivity = { id: 456 };
      const product: IProduct = { id: 39460 };
      productActivity.product = product;

      const productCollection: IProduct[] = [{ id: 79425 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productActivity });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productActivity: IProductActivity = { id: 456 };
      const product: IProduct = { id: 67099 };
      productActivity.product = product;

      activatedRoute.data = of({ productActivity });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productActivity).toEqual(productActivity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductActivity>>();
      const productActivity = { id: 123 };
      jest.spyOn(productActivityFormService, 'getProductActivity').mockReturnValue(productActivity);
      jest.spyOn(productActivityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productActivity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productActivity }));
      saveSubject.complete();

      // THEN
      expect(productActivityFormService.getProductActivity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productActivityService.update).toHaveBeenCalledWith(expect.objectContaining(productActivity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductActivity>>();
      const productActivity = { id: 123 };
      jest.spyOn(productActivityFormService, 'getProductActivity').mockReturnValue({ id: null });
      jest.spyOn(productActivityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productActivity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productActivity }));
      saveSubject.complete();

      // THEN
      expect(productActivityFormService.getProductActivity).toHaveBeenCalled();
      expect(productActivityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductActivity>>();
      const productActivity = { id: 123 };
      jest.spyOn(productActivityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productActivity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productActivityService.update).toHaveBeenCalled();
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
