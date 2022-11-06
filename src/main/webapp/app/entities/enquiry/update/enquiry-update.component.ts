import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EnquiryFormService, EnquiryFormGroup } from './enquiry-form.service';
import { IEnquiry } from '../enquiry.model';
import { EnquiryService } from '../service/enquiry.service';
import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { EnquiryType } from 'app/entities/enumerations/enquiry-type.model';
import { EnquiryResolutionStatus } from 'app/entities/enumerations/enquiry-resolution-status.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'yali-enquiry-update',
  templateUrl: './enquiry-update.component.html',
})
export class EnquiryUpdateComponent implements OnInit {
  account: Account | null = null;
  isSaving = false;
  enquiry: IEnquiry | null = null;
  enquiryTypeValues = Object.keys(EnquiryType);
  enquiryResolutionStatusValues = Object.keys(EnquiryResolutionStatus);

  agentsSharedCollection: IAgent[] = [];
  projectsSharedCollection: IProject[] = [];
  productsSharedCollection: IProduct[] = [];
  customersSharedCollection: ICustomer[] = [];

  editForm: EnquiryFormGroup = this.enquiryFormService.createEnquiryFormGroup();
  private readonly destroy$ = new Subject<void>();

  constructor(
    protected enquiryService: EnquiryService,
    protected enquiryFormService: EnquiryFormService,
    protected agentService: AgentService,
    protected projectService: ProjectService,
    protected productService: ProductService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService
  ) {}

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enquiry }) => {
      this.enquiry = enquiry;
      if (enquiry) {
        this.updateForm(enquiry);
      }

      this.accountService
        .getAuthenticationState()
        .pipe(takeUntil(this.destroy$))
        .subscribe(account => {
          this.account = account;
          if (account != null) {
            this.loadRelationshipsOptions();
          }
        });
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const enquiry = this.enquiryFormService.getEnquiry(this.editForm);
    if (enquiry.id !== null) {
      this.subscribeToSaveResponse(this.enquiryService.update(enquiry));
    } else {
      this.subscribeToSaveResponse(this.enquiryService.create(enquiry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnquiry>>): void {
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

  protected updateForm(enquiry: IEnquiry): void {
    this.enquiry = enquiry;
    this.enquiryFormService.resetForm(this.editForm, enquiry);

    this.agentsSharedCollection = this.agentService.addAgentToCollectionIfMissing<IAgent>(this.agentsSharedCollection, enquiry.agent);
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      enquiry.project
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      enquiry.product
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      enquiry.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.agentService
      .query()
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.enquiry?.agent)))
      .subscribe((agents: IAgent[]) => (this.agentsSharedCollection = agents));

    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.enquiry?.project)))
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.enquiry?.product)))
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) => this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.enquiry?.customer))
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
