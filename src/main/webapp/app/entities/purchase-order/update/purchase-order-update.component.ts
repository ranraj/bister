import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PurchaseOrderFormService, PurchaseOrderFormGroup } from './purchase-order-form.service';
import { IPurchaseOrder } from '../purchase-order.model';
import { PurchaseOrderService } from '../service/purchase-order.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { DeliveryOption } from 'app/entities/enumerations/delivery-option.model';

@Component({
  selector: 'yali-purchase-order-update',
  templateUrl: './purchase-order-update.component.html',
})
export class PurchaseOrderUpdateComponent implements OnInit {
  isSaving = false;
  purchaseOrder: IPurchaseOrder | null = null;
  orderStatusValues = Object.keys(OrderStatus);
  deliveryOptionValues = Object.keys(DeliveryOption);

  usersSharedCollection: IUser[] = [];
  productVariationsSharedCollection: IProductVariation[] = [];
  customersSharedCollection: ICustomer[] = [];

  editForm: PurchaseOrderFormGroup = this.purchaseOrderFormService.createPurchaseOrderFormGroup();

  constructor(
    protected purchaseOrderService: PurchaseOrderService,
    protected purchaseOrderFormService: PurchaseOrderFormService,
    protected userService: UserService,
    protected productVariationService: ProductVariationService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareProductVariation = (o1: IProductVariation | null, o2: IProductVariation | null): boolean =>
    this.productVariationService.compareProductVariation(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseOrder }) => {
      this.purchaseOrder = purchaseOrder;
      if (purchaseOrder) {
        this.updateForm(purchaseOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchaseOrder = this.purchaseOrderFormService.getPurchaseOrder(this.editForm);
    if (purchaseOrder.id !== null) {
      this.subscribeToSaveResponse(this.purchaseOrderService.update(purchaseOrder));
    } else {
      this.subscribeToSaveResponse(this.purchaseOrderService.create(purchaseOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchaseOrder>>): void {
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

  protected updateForm(purchaseOrder: IPurchaseOrder): void {
    this.purchaseOrder = purchaseOrder;
    this.purchaseOrderFormService.resetForm(this.editForm, purchaseOrder);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, purchaseOrder.user);
    this.productVariationsSharedCollection = this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
      this.productVariationsSharedCollection,
      purchaseOrder.productVariation
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      purchaseOrder.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.purchaseOrder?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.productVariationService
      .query()
      .pipe(map((res: HttpResponse<IProductVariation[]>) => res.body ?? []))
      .pipe(
        map((productVariations: IProductVariation[]) =>
          this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
            productVariations,
            this.purchaseOrder?.productVariation
          )
        )
      )
      .subscribe((productVariations: IProductVariation[]) => (this.productVariationsSharedCollection = productVariations));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.purchaseOrder?.customer)
        )
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
