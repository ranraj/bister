import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PaymentScheduleFormService } from './payment-schedule-form.service';
import { PaymentScheduleService } from '../service/payment-schedule.service';
import { IPaymentSchedule } from '../payment-schedule.model';
import { IInvoice } from 'app/entities/invoice/invoice.model';
import { InvoiceService } from 'app/entities/invoice/service/invoice.service';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';

import { PaymentScheduleUpdateComponent } from './payment-schedule-update.component';

describe('PaymentSchedule Management Update Component', () => {
  let comp: PaymentScheduleUpdateComponent;
  let fixture: ComponentFixture<PaymentScheduleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paymentScheduleFormService: PaymentScheduleFormService;
  let paymentScheduleService: PaymentScheduleService;
  let invoiceService: InvoiceService;
  let purchaseOrderService: PurchaseOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PaymentScheduleUpdateComponent],
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
      .overrideTemplate(PaymentScheduleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentScheduleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paymentScheduleFormService = TestBed.inject(PaymentScheduleFormService);
    paymentScheduleService = TestBed.inject(PaymentScheduleService);
    invoiceService = TestBed.inject(InvoiceService);
    purchaseOrderService = TestBed.inject(PurchaseOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call invoice query and add missing value', () => {
      const paymentSchedule: IPaymentSchedule = { id: 456 };
      const invoice: IInvoice = { id: 84539 };
      paymentSchedule.invoice = invoice;

      const invoiceCollection: IInvoice[] = [{ id: 33851 }];
      jest.spyOn(invoiceService, 'query').mockReturnValue(of(new HttpResponse({ body: invoiceCollection })));
      const expectedCollection: IInvoice[] = [invoice, ...invoiceCollection];
      jest.spyOn(invoiceService, 'addInvoiceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paymentSchedule });
      comp.ngOnInit();

      expect(invoiceService.query).toHaveBeenCalled();
      expect(invoiceService.addInvoiceToCollectionIfMissing).toHaveBeenCalledWith(invoiceCollection, invoice);
      expect(comp.invoicesCollection).toEqual(expectedCollection);
    });

    it('Should call PurchaseOrder query and add missing value', () => {
      const paymentSchedule: IPaymentSchedule = { id: 456 };
      const purchaseOrdep: IPurchaseOrder = { id: 49372 };
      paymentSchedule.purchaseOrdep = purchaseOrdep;

      const purchaseOrderCollection: IPurchaseOrder[] = [{ id: 78102 }];
      jest.spyOn(purchaseOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseOrderCollection })));
      const additionalPurchaseOrders = [purchaseOrdep];
      const expectedCollection: IPurchaseOrder[] = [...additionalPurchaseOrders, ...purchaseOrderCollection];
      jest.spyOn(purchaseOrderService, 'addPurchaseOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paymentSchedule });
      comp.ngOnInit();

      expect(purchaseOrderService.query).toHaveBeenCalled();
      expect(purchaseOrderService.addPurchaseOrderToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseOrderCollection,
        ...additionalPurchaseOrders.map(expect.objectContaining)
      );
      expect(comp.purchaseOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const paymentSchedule: IPaymentSchedule = { id: 456 };
      const invoice: IInvoice = { id: 16028 };
      paymentSchedule.invoice = invoice;
      const purchaseOrdep: IPurchaseOrder = { id: 24435 };
      paymentSchedule.purchaseOrdep = purchaseOrdep;

      activatedRoute.data = of({ paymentSchedule });
      comp.ngOnInit();

      expect(comp.invoicesCollection).toContain(invoice);
      expect(comp.purchaseOrdersSharedCollection).toContain(purchaseOrdep);
      expect(comp.paymentSchedule).toEqual(paymentSchedule);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentSchedule>>();
      const paymentSchedule = { id: 123 };
      jest.spyOn(paymentScheduleFormService, 'getPaymentSchedule').mockReturnValue(paymentSchedule);
      jest.spyOn(paymentScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentSchedule }));
      saveSubject.complete();

      // THEN
      expect(paymentScheduleFormService.getPaymentSchedule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paymentScheduleService.update).toHaveBeenCalledWith(expect.objectContaining(paymentSchedule));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentSchedule>>();
      const paymentSchedule = { id: 123 };
      jest.spyOn(paymentScheduleFormService, 'getPaymentSchedule').mockReturnValue({ id: null });
      jest.spyOn(paymentScheduleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentSchedule: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentSchedule }));
      saveSubject.complete();

      // THEN
      expect(paymentScheduleFormService.getPaymentSchedule).toHaveBeenCalled();
      expect(paymentScheduleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentSchedule>>();
      const paymentSchedule = { id: 123 };
      jest.spyOn(paymentScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paymentScheduleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareInvoice', () => {
      it('Should forward to invoiceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(invoiceService, 'compareInvoice');
        comp.compareInvoice(entity, entity2);
        expect(invoiceService.compareInvoice).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePurchaseOrder', () => {
      it('Should forward to purchaseOrderService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(purchaseOrderService, 'comparePurchaseOrder');
        comp.comparePurchaseOrder(entity, entity2);
        expect(purchaseOrderService.comparePurchaseOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
