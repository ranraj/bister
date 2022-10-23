import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BookingOrderFormService, BookingOrderFormGroup } from './booking-order-form.service';
import { IBookingOrder } from '../booking-order.model';
import { BookingOrderService } from '../service/booking-order.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';
import { BookingOrderStatus } from 'app/entities/enumerations/booking-order-status.model';

@Component({
  selector: 'yali-booking-order-update',
  templateUrl: './booking-order-update.component.html',
})
export class BookingOrderUpdateComponent implements OnInit {
  isSaving = false;
  bookingOrder: IBookingOrder | null = null;
  bookingOrderStatusValues = Object.keys(BookingOrderStatus);

  customersSharedCollection: ICustomer[] = [];
  productVariationsSharedCollection: IProductVariation[] = [];

  editForm: BookingOrderFormGroup = this.bookingOrderFormService.createBookingOrderFormGroup();

  constructor(
    protected bookingOrderService: BookingOrderService,
    protected bookingOrderFormService: BookingOrderFormService,
    protected customerService: CustomerService,
    protected productVariationService: ProductVariationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  compareProductVariation = (o1: IProductVariation | null, o2: IProductVariation | null): boolean =>
    this.productVariationService.compareProductVariation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookingOrder }) => {
      this.bookingOrder = bookingOrder;
      if (bookingOrder) {
        this.updateForm(bookingOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookingOrder = this.bookingOrderFormService.getBookingOrder(this.editForm);
    if (bookingOrder.id !== null) {
      this.subscribeToSaveResponse(this.bookingOrderService.update(bookingOrder));
    } else {
      this.subscribeToSaveResponse(this.bookingOrderService.create(bookingOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookingOrder>>): void {
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

  protected updateForm(bookingOrder: IBookingOrder): void {
    this.bookingOrder = bookingOrder;
    this.bookingOrderFormService.resetForm(this.editForm, bookingOrder);

    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      bookingOrder.customer
    );
    this.productVariationsSharedCollection = this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
      this.productVariationsSharedCollection,
      bookingOrder.productVariation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.bookingOrder?.customer)
        )
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));

    this.productVariationService
      .query()
      .pipe(map((res: HttpResponse<IProductVariation[]>) => res.body ?? []))
      .pipe(
        map((productVariations: IProductVariation[]) =>
          this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
            productVariations,
            this.bookingOrder?.productVariation
          )
        )
      )
      .subscribe((productVariations: IProductVariation[]) => (this.productVariationsSharedCollection = productVariations));
  }
}
