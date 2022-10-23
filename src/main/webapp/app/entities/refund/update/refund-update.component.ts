import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RefundFormService, RefundFormGroup } from './refund-form.service';
import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { RefundStatus } from 'app/entities/enumerations/refund-status.model';

@Component({
  selector: 'yali-refund-update',
  templateUrl: './refund-update.component.html',
})
export class RefundUpdateComponent implements OnInit {
  isSaving = false;
  refund: IRefund | null = null;
  refundStatusValues = Object.keys(RefundStatus);

  transactionsSharedCollection: ITransaction[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: RefundFormGroup = this.refundFormService.createRefundFormGroup();

  constructor(
    protected refundService: RefundService,
    protected refundFormService: RefundFormService,
    protected transactionService: TransactionService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refund }) => {
      this.refund = refund;
      if (refund) {
        this.updateForm(refund);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const refund = this.refundFormService.getRefund(this.editForm);
    if (refund.id !== null) {
      this.subscribeToSaveResponse(this.refundService.update(refund));
    } else {
      this.subscribeToSaveResponse(this.refundService.create(refund));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRefund>>): void {
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

  protected updateForm(refund: IRefund): void {
    this.refund = refund;
    this.refundFormService.resetForm(this.editForm, refund);

    this.transactionsSharedCollection = this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(
      this.transactionsSharedCollection,
      refund.transaction
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, refund.user);
  }

  protected loadRelationshipsOptions(): void {
    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.refund?.transaction)
        )
      )
      .subscribe((transactions: ITransaction[]) => (this.transactionsSharedCollection = transactions));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.refund?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
