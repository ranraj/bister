import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductSpecificationGroupFormService } from './product-specification-group-form.service';
import { ProductSpecificationGroupService } from '../service/product-specification-group.service';
import { IProductSpecificationGroup } from '../product-specification-group.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductSpecificationGroupUpdateComponent } from './product-specification-group-update.component';

describe('ProductSpecificationGroup Management Update Component', () => {
  let comp: ProductSpecificationGroupUpdateComponent;
  let fixture: ComponentFixture<ProductSpecificationGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productSpecificationGroupFormService: ProductSpecificationGroupFormService;
  let productSpecificationGroupService: ProductSpecificationGroupService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductSpecificationGroupUpdateComponent],
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
      .overrideTemplate(ProductSpecificationGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductSpecificationGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productSpecificationGroupFormService = TestBed.inject(ProductSpecificationGroupFormService);
    productSpecificationGroupService = TestBed.inject(ProductSpecificationGroupService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const productSpecificationGroup: IProductSpecificationGroup = { id: 456 };
      const product: IProduct = { id: 65392 };
      productSpecificationGroup.product = product;

      const productCollection: IProduct[] = [{ id: 5134 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productSpecificationGroup });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productSpecificationGroup: IProductSpecificationGroup = { id: 456 };
      const product: IProduct = { id: 49016 };
      productSpecificationGroup.product = product;

      activatedRoute.data = of({ productSpecificationGroup });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productSpecificationGroup).toEqual(productSpecificationGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecificationGroup>>();
      const productSpecificationGroup = { id: 123 };
      jest.spyOn(productSpecificationGroupFormService, 'getProductSpecificationGroup').mockReturnValue(productSpecificationGroup);
      jest.spyOn(productSpecificationGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecificationGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productSpecificationGroup }));
      saveSubject.complete();

      // THEN
      expect(productSpecificationGroupFormService.getProductSpecificationGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productSpecificationGroupService.update).toHaveBeenCalledWith(expect.objectContaining(productSpecificationGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecificationGroup>>();
      const productSpecificationGroup = { id: 123 };
      jest.spyOn(productSpecificationGroupFormService, 'getProductSpecificationGroup').mockReturnValue({ id: null });
      jest.spyOn(productSpecificationGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecificationGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productSpecificationGroup }));
      saveSubject.complete();

      // THEN
      expect(productSpecificationGroupFormService.getProductSpecificationGroup).toHaveBeenCalled();
      expect(productSpecificationGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecificationGroup>>();
      const productSpecificationGroup = { id: 123 };
      jest.spyOn(productSpecificationGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecificationGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productSpecificationGroupService.update).toHaveBeenCalled();
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
