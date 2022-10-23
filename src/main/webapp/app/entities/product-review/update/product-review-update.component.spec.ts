import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductReviewFormService } from './product-review-form.service';
import { ProductReviewService } from '../service/product-review.service';
import { IProductReview } from '../product-review.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductReviewUpdateComponent } from './product-review-update.component';

describe('ProductReview Management Update Component', () => {
  let comp: ProductReviewUpdateComponent;
  let fixture: ComponentFixture<ProductReviewUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productReviewFormService: ProductReviewFormService;
  let productReviewService: ProductReviewService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductReviewUpdateComponent],
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
      .overrideTemplate(ProductReviewUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductReviewUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productReviewFormService = TestBed.inject(ProductReviewFormService);
    productReviewService = TestBed.inject(ProductReviewService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const productReview: IProductReview = { id: 456 };
      const product: IProduct = { id: 23695 };
      productReview.product = product;

      const productCollection: IProduct[] = [{ id: 60198 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productReview });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productReview: IProductReview = { id: 456 };
      const product: IProduct = { id: 84236 };
      productReview.product = product;

      activatedRoute.data = of({ productReview });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productReview).toEqual(productReview);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReview>>();
      const productReview = { id: 123 };
      jest.spyOn(productReviewFormService, 'getProductReview').mockReturnValue(productReview);
      jest.spyOn(productReviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productReview }));
      saveSubject.complete();

      // THEN
      expect(productReviewFormService.getProductReview).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productReviewService.update).toHaveBeenCalledWith(expect.objectContaining(productReview));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReview>>();
      const productReview = { id: 123 };
      jest.spyOn(productReviewFormService, 'getProductReview').mockReturnValue({ id: null });
      jest.spyOn(productReviewService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReview: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productReview }));
      saveSubject.complete();

      // THEN
      expect(productReviewFormService.getProductReview).toHaveBeenCalled();
      expect(productReviewService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReview>>();
      const productReview = { id: 123 };
      jest.spyOn(productReviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productReviewService.update).toHaveBeenCalled();
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
