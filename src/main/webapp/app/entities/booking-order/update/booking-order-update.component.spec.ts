import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BookingOrderFormService } from './booking-order-form.service';
import { BookingOrderService } from '../service/booking-order.service';
import { IBookingOrder } from '../booking-order.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';

import { BookingOrderUpdateComponent } from './booking-order-update.component';

describe('BookingOrder Management Update Component', () => {
  let comp: BookingOrderUpdateComponent;
  let fixture: ComponentFixture<BookingOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookingOrderFormService: BookingOrderFormService;
  let bookingOrderService: BookingOrderService;
  let customerService: CustomerService;
  let productVariationService: ProductVariationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BookingOrderUpdateComponent],
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
      .overrideTemplate(BookingOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookingOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookingOrderFormService = TestBed.inject(BookingOrderFormService);
    bookingOrderService = TestBed.inject(BookingOrderService);
    customerService = TestBed.inject(CustomerService);
    productVariationService = TestBed.inject(ProductVariationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customer query and add missing value', () => {
      const bookingOrder: IBookingOrder = { id: 456 };
      const customer: ICustomer = { id: 91456 };
      bookingOrder.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 8508 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingOrder });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProductVariation query and add missing value', () => {
      const bookingOrder: IBookingOrder = { id: 456 };
      const productVariation: IProductVariation = { id: 94553 };
      bookingOrder.productVariation = productVariation;

      const productVariationCollection: IProductVariation[] = [{ id: 73878 }];
      jest.spyOn(productVariationService, 'query').mockReturnValue(of(new HttpResponse({ body: productVariationCollection })));
      const additionalProductVariations = [productVariation];
      const expectedCollection: IProductVariation[] = [...additionalProductVariations, ...productVariationCollection];
      jest.spyOn(productVariationService, 'addProductVariationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingOrder });
      comp.ngOnInit();

      expect(productVariationService.query).toHaveBeenCalled();
      expect(productVariationService.addProductVariationToCollectionIfMissing).toHaveBeenCalledWith(
        productVariationCollection,
        ...additionalProductVariations.map(expect.objectContaining)
      );
      expect(comp.productVariationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookingOrder: IBookingOrder = { id: 456 };
      const customer: ICustomer = { id: 95673 };
      bookingOrder.customer = customer;
      const productVariation: IProductVariation = { id: 16347 };
      bookingOrder.productVariation = productVariation;

      activatedRoute.data = of({ bookingOrder });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.productVariationsSharedCollection).toContain(productVariation);
      expect(comp.bookingOrder).toEqual(bookingOrder);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingOrder>>();
      const bookingOrder = { id: 123 };
      jest.spyOn(bookingOrderFormService, 'getBookingOrder').mockReturnValue(bookingOrder);
      jest.spyOn(bookingOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookingOrder }));
      saveSubject.complete();

      // THEN
      expect(bookingOrderFormService.getBookingOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookingOrderService.update).toHaveBeenCalledWith(expect.objectContaining(bookingOrder));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingOrder>>();
      const bookingOrder = { id: 123 };
      jest.spyOn(bookingOrderFormService, 'getBookingOrder').mockReturnValue({ id: null });
      jest.spyOn(bookingOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingOrder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookingOrder }));
      saveSubject.complete();

      // THEN
      expect(bookingOrderFormService.getBookingOrder).toHaveBeenCalled();
      expect(bookingOrderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingOrder>>();
      const bookingOrder = { id: 123 };
      jest.spyOn(bookingOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookingOrderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
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
  });
});
