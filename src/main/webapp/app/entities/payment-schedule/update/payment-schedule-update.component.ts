import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PaymentScheduleFormService, PaymentScheduleFormGroup } from './payment-schedule-form.service';
import { IPaymentSchedule } from '../payment-schedule.model';
import { PaymentScheduleService } from '../service/payment-schedule.service';
import { IInvoice } from 'app/entities/invoice/invoice.model';
import { InvoiceService } from 'app/entities/invoice/service/invoice.service';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';
import { PaymentScheduleStatus } from 'app/entities/enumerations/payment-schedule-status.model';

@Component({
  selector: 'yali-payment-schedule-update',
  templateUrl: './payment-schedule-update.component.html',
})
export class PaymentScheduleUpdateComponent implements OnInit {
  isSaving = false;
  paymentSchedule: IPaymentSchedule | null = null;
  paymentScheduleStatusValues = Object.keys(PaymentScheduleStatus);

  invoicesCollection: IInvoice[] = [];
  purchaseOrdersSharedCollection: IPurchaseOrder[] = [];

  editForm: PaymentScheduleFormGroup = this.paymentScheduleFormService.createPaymentScheduleFormGroup();

  constructor(
    protected paymentScheduleService: PaymentScheduleService,
    protected paymentScheduleFormService: PaymentScheduleFormService,
    protected invoiceService: InvoiceService,
    protected purchaseOrderService: PurchaseOrderService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareInvoice = (o1: IInvoice | null, o2: IInvoice | null): boolean => this.invoiceService.compareInvoice(o1, o2);

  comparePurchaseOrder = (o1: IPurchaseOrder | null, o2: IPurchaseOrder | null): boolean =>
    this.purchaseOrderService.comparePurchaseOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentSchedule }) => {
      this.paymentSchedule = paymentSchedule;
      if (paymentSchedule) {
        this.updateForm(paymentSchedule);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paymentSchedule = this.paymentScheduleFormService.getPaymentSchedule(this.editForm);
    if (paymentSchedule.id !== null) {
      this.subscribeToSaveResponse(this.paymentScheduleService.update(paymentSchedule));
    } else {
      this.subscribeToSaveResponse(this.paymentScheduleService.create(paymentSchedule));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentSchedule>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(paymentSchedule: IPaymentSchedule): void {
    this.paymentSchedule = paymentSchedule;
    this.paymentScheduleFormService.resetForm(this.editForm, paymentSchedule);

    this.invoicesCollection = this.invoiceService.addInvoiceToCollectionIfMissing<IInvoice>(
      this.invoicesCollection,
      paymentSchedule.invoice
    );
    this.purchaseOrdersSharedCollection = this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(
      this.purchaseOrdersSharedCollection,
      paymentSchedule.purchaseOrdep
    );
  }

  protected loadRelationshipsOptions(): void {
    this.invoiceService
      .query({ filter: 'paymentschedule-is-null' })
      .pipe(map((res: HttpResponse<IInvoice[]>) => res.body ?? []))
      .pipe(
        map((invoices: IInvoice[]) =>
          this.invoiceService.addInvoiceToCollectionIfMissing<IInvoice>(invoices, this.paymentSchedule?.invoice)
        )
      )
      .subscribe((invoices: IInvoice[]) => (this.invoicesCollection = invoices));

    this.purchaseOrderService
      .query()
      .pipe(map((res: HttpResponse<IPurchaseOrder[]>) => res.body ?? []))
      .pipe(
        map((purchaseOrders: IPurchaseOrder[]) =>
          this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(
            purchaseOrders,
            this.paymentSchedule?.purchaseOrdep
          )
        )
      )
      .subscribe((purchaseOrders: IPurchaseOrder[]) => (this.purchaseOrdersSharedCollection = purchaseOrders));
  }
}
