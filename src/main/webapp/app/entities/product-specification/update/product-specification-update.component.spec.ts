import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductSpecificationFormService } from './product-specification-form.service';
import { ProductSpecificationService } from '../service/product-specification.service';
import { IProductSpecification } from '../product-specification.model';
import { IProductSpecificationGroup } from 'app/entities/product-specification-group/product-specification-group.model';
import { ProductSpecificationGroupService } from 'app/entities/product-specification-group/service/product-specification-group.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { ProductSpecificationUpdateComponent } from './product-specification-update.component';

describe('ProductSpecification Management Update Component', () => {
  let comp: ProductSpecificationUpdateComponent;
  let fixture: ComponentFixture<ProductSpecificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productSpecificationFormService: ProductSpecificationFormService;
  let productSpecificationService: ProductSpecificationService;
  let productSpecificationGroupService: ProductSpecificationGroupService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductSpecificationUpdateComponent],
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
      .overrideTemplate(ProductSpecificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductSpecificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productSpecificationFormService = TestBed.inject(ProductSpecificationFormService);
    productSpecificationService = TestBed.inject(ProductSpecificationService);
    productSpecificationGroupService = TestBed.inject(ProductSpecificationGroupService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProductSpecificationGroup query and add missing value', () => {
      const productSpecification: IProductSpecification = { id: 456 };
      const productSpecificationGroup: IProductSpecificationGroup = { id: 65030 };
      productSpecification.productSpecificationGroup = productSpecificationGroup;

      const productSpecificationGroupCollection: IProductSpecificationGroup[] = [{ id: 16427 }];
      jest
        .spyOn(productSpecificationGroupService, 'query')
        .mockReturnValue(of(new HttpResponse({ body: productSpecificationGroupCollection })));
      const additionalProductSpecificationGroups = [productSpecificationGroup];
      const expectedCollection: IProductSpecificationGroup[] = [
        ...additionalProductSpecificationGroups,
        ...productSpecificationGroupCollection,
      ];
      jest.spyOn(productSpecificationGroupService, 'addProductSpecificationGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productSpecification });
      comp.ngOnInit();

      expect(productSpecificationGroupService.query).toHaveBeenCalled();
      expect(productSpecificationGroupService.addProductSpecificationGroupToCollectionIfMissing).toHaveBeenCalledWith(
        productSpecificationGroupCollection,
        ...additionalProductSpecificationGroups.map(expect.objectContaining)
      );
      expect(comp.productSpecificationGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const productSpecification: IProductSpecification = { id: 456 };
      const product: IProduct = { id: 43910 };
      productSpecification.product = product;

      const productCollection: IProduct[] = [{ id: 19651 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productSpecification });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productSpecification: IProductSpecification = { id: 456 };
      const productSpecificationGroup: IProductSpecificationGroup = { id: 85196 };
      productSpecification.productSpecificationGroup = productSpecificationGroup;
      const product: IProduct = { id: 76101 };
      productSpecification.product = product;

      activatedRoute.data = of({ productSpecification });
      comp.ngOnInit();

      expect(comp.productSpecificationGroupsSharedCollection).toContain(productSpecificationGroup);
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productSpecification).toEqual(productSpecification);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecification>>();
      const productSpecification = { id: 123 };
      jest.spyOn(productSpecificationFormService, 'getProductSpecification').mockReturnValue(productSpecification);
      jest.spyOn(productSpecificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productSpecification }));
      saveSubject.complete();

      // THEN
      expect(productSpecificationFormService.getProductSpecification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productSpecificationService.update).toHaveBeenCalledWith(expect.objectContaining(productSpecification));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecification>>();
      const productSpecification = { id: 123 };
      jest.spyOn(productSpecificationFormService, 'getProductSpecification').mockReturnValue({ id: null });
      jest.spyOn(productSpecificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productSpecification }));
      saveSubject.complete();

      // THEN
      expect(productSpecificationFormService.getProductSpecification).toHaveBeenCalled();
      expect(productSpecificationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductSpecification>>();
      const productSpecification = { id: 123 };
      jest.spyOn(productSpecificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productSpecification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productSpecificationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProductSpecificationGroup', () => {
      it('Should forward to productSpecificationGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productSpecificationGroupService, 'compareProductSpecificationGroup');
        comp.compareProductSpecificationGroup(entity, entity2);
        expect(productSpecificationGroupService.compareProductSpecificationGroup).toHaveBeenCalledWith(entity, entity2);
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
