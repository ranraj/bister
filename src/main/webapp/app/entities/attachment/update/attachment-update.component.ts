import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AttachmentFormService, AttachmentFormGroup } from './attachment-form.service';
import { IAttachment } from '../attachment.model';
import { AttachmentService } from '../service/attachment.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IEnquiry } from 'app/entities/enquiry/enquiry.model';
import { EnquiryService } from 'app/entities/enquiry/service/enquiry.service';
import { ICertification } from 'app/entities/certification/certification.model';
import { CertificationService } from 'app/entities/certification/service/certification.service';
import { IProductSpecification } from 'app/entities/product-specification/product-specification.model';
import { ProductSpecificationService } from 'app/entities/product-specification/service/product-specification.service';
import { IProjectSpecification } from 'app/entities/project-specification/project-specification.model';
import { ProjectSpecificationService } from 'app/entities/project-specification/service/project-specification.service';
import { AttachmentType } from 'app/entities/enumerations/attachment-type.model';
import { AttachmentApprovalStatus } from 'app/entities/enumerations/attachment-approval-status.model';
import { AttachmentSourceType } from 'app/entities/enumerations/attachment-source-type.model';
import { AttachmentVisibilityType } from 'app/entities/enumerations/attachment-visibility-type.model';

@Component({
  selector: 'yali-attachment-update',
  templateUrl: './attachment-update.component.html',
})
export class AttachmentUpdateComponent implements OnInit {
  isSaving = false;
  attachment: IAttachment | null = null;
  attachmentTypeValues = Object.keys(AttachmentType);
  attachmentApprovalStatusValues = Object.keys(AttachmentApprovalStatus);
  attachmentSourceTypeValues = Object.keys(AttachmentSourceType);
  attachmentVisibilityTypeValues = Object.keys(AttachmentVisibilityType);

  productsSharedCollection: IProduct[] = [];
  projectsSharedCollection: IProject[] = [];
  enquiriesSharedCollection: IEnquiry[] = [];
  certificationsSharedCollection: ICertification[] = [];
  productSpecificationsSharedCollection: IProductSpecification[] = [];
  projectSpecificationsSharedCollection: IProjectSpecification[] = [];

  editForm: AttachmentFormGroup = this.attachmentFormService.createAttachmentFormGroup();

  constructor(
    protected attachmentService: AttachmentService,
    protected attachmentFormService: AttachmentFormService,
    protected productService: ProductService,
    protected projectService: ProjectService,
    protected enquiryService: EnquiryService,
    protected certificationService: CertificationService,
    protected productSpecificationService: ProductSpecificationService,
    protected projectSpecificationService: ProjectSpecificationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  compareEnquiry = (o1: IEnquiry | null, o2: IEnquiry | null): boolean => this.enquiryService.compareEnquiry(o1, o2);

  compareCertification = (o1: ICertification | null, o2: ICertification | null): boolean =>
    this.certificationService.compareCertification(o1, o2);

  compareProductSpecification = (o1: IProductSpecification | null, o2: IProductSpecification | null): boolean =>
    this.productSpecificationService.compareProductSpecification(o1, o2);

  compareProjectSpecification = (o1: IProjectSpecification | null, o2: IProjectSpecification | null): boolean =>
    this.projectSpecificationService.compareProjectSpecification(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attachment }) => {
      this.attachment = attachment;
      if (attachment) {
        this.updateForm(attachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const attachment = this.attachmentFormService.getAttachment(this.editForm);
    if (attachment.id !== null) {
      this.subscribeToSaveResponse(this.attachmentService.update(attachment));
    } else {
      this.subscribeToSaveResponse(this.attachmentService.create(attachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttachment>>): void {
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

  protected updateForm(attachment: IAttachment): void {
    this.attachment = attachment;
    this.attachmentFormService.resetForm(this.editForm, attachment);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      attachment.product
    );
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      attachment.project
    );
    this.enquiriesSharedCollection = this.enquiryService.addEnquiryToCollectionIfMissing<IEnquiry>(
      this.enquiriesSharedCollection,
      attachment.enquiry
    );
    this.certificationsSharedCollection = this.certificationService.addCertificationToCollectionIfMissing<ICertification>(
      this.certificationsSharedCollection,
      attachment.certification
    );
    this.productSpecificationsSharedCollection =
      this.productSpecificationService.addProductSpecificationToCollectionIfMissing<IProductSpecification>(
        this.productSpecificationsSharedCollection,
        attachment.productSpecification
      );
    this.projectSpecificationsSharedCollection =
      this.projectSpecificationService.addProjectSpecificationToCollectionIfMissing<IProjectSpecification>(
        this.projectSpecificationsSharedCollection,
        attachment.projectSpecification
      );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.attachment?.product))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.attachment?.project))
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));

    this.enquiryService
      .query()
      .pipe(map((res: HttpResponse<IEnquiry[]>) => res.body ?? []))
      .pipe(
        map((enquiries: IEnquiry[]) => this.enquiryService.addEnquiryToCollectionIfMissing<IEnquiry>(enquiries, this.attachment?.enquiry))
      )
      .subscribe((enquiries: IEnquiry[]) => (this.enquiriesSharedCollection = enquiries));

    this.certificationService
      .query()
      .pipe(map((res: HttpResponse<ICertification[]>) => res.body ?? []))
      .pipe(
        map((certifications: ICertification[]) =>
          this.certificationService.addCertificationToCollectionIfMissing<ICertification>(certifications, this.attachment?.certification)
        )
      )
      .subscribe((certifications: ICertification[]) => (this.certificationsSharedCollection = certifications));

    this.productSpecificationService
      .query()
      .pipe(map((res: HttpResponse<IProductSpecification[]>) => res.body ?? []))
      .pipe(
        map((productSpecifications: IProductSpecification[]) =>
          this.productSpecificationService.addProductSpecificationToCollectionIfMissing<IProductSpecification>(
            productSpecifications,
            this.attachment?.productSpecification
          )
        )
      )
      .subscribe((productSpecifications: IProductSpecification[]) => (this.productSpecificationsSharedCollection = productSpecifications));

    this.projectSpecificationService
      .query()
      .pipe(map((res: HttpResponse<IProjectSpecification[]>) => res.body ?? []))
      .pipe(
        map((projectSpecifications: IProjectSpecification[]) =>
          this.projectSpecificationService.addProjectSpecificationToCollectionIfMissing<IProjectSpecification>(
            projectSpecifications,
            this.attachment?.projectSpecification
          )
        )
      )
      .subscribe((projectSpecifications: IProjectSpecification[]) => (this.projectSpecificationsSharedCollection = projectSpecifications));
  }
}
