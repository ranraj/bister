import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PurchaseOrderFormService } from './purchase-order-form.service';
import { PurchaseOrderService } from '../service/purchase-order.service';
import { IPurchaseOrder } from '../purchase-order.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

import { PurchaseOrderUpdateComponent } from './purchase-order-update.component';

describe('PurchaseOrder Management Update Component', () => {
  let comp: PurchaseOrderUpdateComponent;
  let fixture: ComponentFixture<PurchaseOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchaseOrderFormService: PurchaseOrderFormService;
  let purchaseOrderService: PurchaseOrderService;
  let userService: UserService;
  let productVariationService: ProductVariationService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PurchaseOrderUpdateComponent],
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
      .overrideTemplate(PurchaseOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchaseOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchaseOrderFormService = TestBed.inject(PurchaseOrderFormService);
    purchaseOrderService = TestBed.inject(PurchaseOrderService);
    userService = TestBed.inject(UserService);
    productVariationService = TestBed.inject(ProductVariationService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const purchaseOrder: IPurchaseOrder = { id: 456 };
      const user: IUser = { id: 26842 };
      purchaseOrder.user = user;

      const userCollection: IUser[] = [{ id: 27473 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProductVariation query and add missing value', () => {
      const purchaseOrder: IPurchaseOrder = { id: 456 };
      const productVariation: IProductVariation = { id: 72003 };
      purchaseOrder.productVariation = productVariation;

      const productVariationCollection: IProductVariation[] = [{ id: 22767 }];
      jest.spyOn(productVariationService, 'query').mockReturnValue(of(new HttpResponse({ body: productVariationCollection })));
      const additionalProductVariations = [productVariation];
      const expectedCollection: IProductVariation[] = [...additionalProductVariations, ...productVariationCollection];
      jest.spyOn(productVariationService, 'addProductVariationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      expect(productVariationService.query).toHaveBeenCalled();
      expect(productVariationService.addProductVariationToCollectionIfMissing).toHaveBeenCalledWith(
        productVariationCollection,
        ...additionalProductVariations.map(expect.objectContaining)
      );
      expect(comp.productVariationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const purchaseOrder: IPurchaseOrder = { id: 456 };
      const customer: ICustomer = { id: 96747 };
      purchaseOrder.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 60210 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const purchaseOrder: IPurchaseOrder = { id: 456 };
      const user: IUser = { id: 90200 };
      purchaseOrder.user = user;
      const productVariation: IProductVariation = { id: 2814 };
      purchaseOrder.productVariation = productVariation;
      const customer: ICustomer = { id: 76407 };
      purchaseOrder.customer = customer;

      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.productVariationsSharedCollection).toContain(productVariation);
      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.purchaseOrder).toEqual(purchaseOrder);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOrder>>();
      const purchaseOrder = { id: 123 };
      jest.spyOn(purchaseOrderFormService, 'getPurchaseOrder').mockReturnValue(purchaseOrder);
      jest.spyOn(purchaseOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchaseOrder }));
      saveSubject.complete();

      // THEN
      expect(purchaseOrderFormService.getPurchaseOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchaseOrderService.update).toHaveBeenCalledWith(expect.objectContaining(purchaseOrder));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOrder>>();
      const purchaseOrder = { id: 123 };
      jest.spyOn(purchaseOrderFormService, 'getPurchaseOrder').mockReturnValue({ id: null });
      jest.spyOn(purchaseOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOrder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchaseOrder }));
      saveSubject.complete();

      // THEN
      expect(purchaseOrderFormService.getPurchaseOrder).toHaveBeenCalled();
      expect(purchaseOrderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOrder>>();
      const purchaseOrder = { id: 123 };
      jest.spyOn(purchaseOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchaseOrderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProductVariation', () => {
      it('Should forward to productVariationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productVariationService, 'compareProductVariation');
        comp.compareProductVariation(entity, entity2);
        expect(productVariationService.compareProductVariation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
