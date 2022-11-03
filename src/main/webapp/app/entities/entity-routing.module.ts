import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'business-partner',
        data: { pageTitle: 'bisterApp.businessPartner.home.title' },
        loadChildren: () => import('./business-partner/business-partner.module').then(m => m.BusinessPartnerModule),
      },
      {
        path: 'organisation',
        data: { pageTitle: 'bisterApp.organisation.home.title' },
        loadChildren: () => import('./organisation/organisation.module').then(m => m.OrganisationModule),
      },
      {
        path: 'facility',
        data: { pageTitle: 'bisterApp.facility.home.title' },
        loadChildren: () => import('./facility/facility.module').then(m => m.FacilityModule),
      },
      {
        path: 'customer',
        data: { pageTitle: 'bisterApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule),
      },
      {
        path: 'agent',
        data: { pageTitle: 'bisterApp.agent.home.title' },
        loadChildren: () => import('./agent/agent.module').then(m => m.AgentModule),
      },
      {
        path: 'project-type',
        data: { pageTitle: 'bisterApp.projectType.home.title' },
        loadChildren: () => import('./project-type/project-type.module').then(m => m.ProjectTypeModule),
      },
      {
        path: 'project',
        data: { pageTitle: 'bisterApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: '',
        data: { pageTitle: 'bisterApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'project-activity',
        data: { pageTitle: 'bisterApp.projectActivity.home.title' },
        loadChildren: () => import('./project-activity/project-activity.module').then(m => m.ProjectActivityModule),
      },
      {
        path: 'product-activity',
        data: { pageTitle: 'bisterApp.productActivity.home.title' },
        loadChildren: () => import('./product-activity/product-activity.module').then(m => m.ProductActivityModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'bisterApp.product.home.title' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'bisterApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'bisterApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'product-attribute',
        data: { pageTitle: 'bisterApp.productAttribute.home.title' },
        loadChildren: () => import('./product-attribute/product-attribute.module').then(m => m.ProductAttributeModule),
      },
      {
        path: 'product-attribute-term',
        data: { pageTitle: 'bisterApp.productAttributeTerm.home.title' },
        loadChildren: () => import('./product-attribute-term/product-attribute-term.module').then(m => m.ProductAttributeTermModule),
      },
      {
        path: 'product-variation-attribute-term',
        data: { pageTitle: 'bisterApp.productVariationAttributeTerm.home.title' },
        loadChildren: () =>
          import('./product-variation-attribute-term/product-variation-attribute-term.module').then(
            m => m.ProductVariationAttributeTermModule
          ),
      },
      {
        path: 'product-review',
        data: { pageTitle: 'bisterApp.productReview.home.title' },
        loadChildren: () => import('./product-review/product-review.module').then(m => m.ProductReviewModule),
      },
      {
        path: 'project-review',
        data: { pageTitle: 'bisterApp.projectReview.home.title' },
        loadChildren: () => import('./project-review/project-review.module').then(m => m.ProjectReviewModule),
      },
      {
        path: 'product-variation',
        data: { pageTitle: 'bisterApp.productVariation.home.title' },
        loadChildren: () => import('./product-variation/product-variation.module').then(m => m.ProductVariationModule),
      },
      {
        path: 'project-specification-group',
        data: { pageTitle: 'bisterApp.projectSpecificationGroup.home.title' },
        loadChildren: () =>
          import('./project-specification-group/project-specification-group.module').then(m => m.ProjectSpecificationGroupModule),
      },
      {
        path: 'product-specification-group',
        data: { pageTitle: 'bisterApp.productSpecificationGroup.home.title' },
        loadChildren: () =>
          import('./product-specification-group/product-specification-group.module').then(m => m.ProductSpecificationGroupModule),
      },
      {
        path: 'project-specification',
        data: { pageTitle: 'bisterApp.projectSpecification.home.title' },
        loadChildren: () => import('./project-specification/project-specification.module').then(m => m.ProjectSpecificationModule),
      },
      {
        path: 'product-specification',
        data: { pageTitle: 'bisterApp.productSpecification.home.title' },
        loadChildren: () => import('./product-specification/product-specification.module').then(m => m.ProductSpecificationModule),
      },
      {
        path: 'attachment',
        data: { pageTitle: 'bisterApp.attachment.home.title' },
        loadChildren: () => import('./attachment/attachment.module').then(m => m.AttachmentModule),
      },
      {
        path: 'certification',
        data: { pageTitle: 'bisterApp.certification.home.title' },
        loadChildren: () => import('./certification/certification.module').then(m => m.CertificationModule),
      },
      {
        path: 'promotion',
        data: { pageTitle: 'bisterApp.promotion.home.title' },
        loadChildren: () => import('./promotion/promotion.module').then(m => m.PromotionModule),
      },
      {
        path: 'payment-schedule',
        data: { pageTitle: 'bisterApp.paymentSchedule.home.title' },
        loadChildren: () => import('./payment-schedule/payment-schedule.module').then(m => m.PaymentScheduleModule),
      },
      {
        path: 'purchase-order',
        data: { pageTitle: 'bisterApp.purchaseOrder.home.title' },
        loadChildren: () => import('./purchase-order/purchase-order.module').then(m => m.PurchaseOrderModule),
      },
      {
        path: 'booking-order',
        data: { pageTitle: 'bisterApp.bookingOrder.home.title' },
        loadChildren: () => import('./booking-order/booking-order.module').then(m => m.BookingOrderModule),
      },
      {
        path: 'address',
        data: { pageTitle: 'bisterApp.address.home.title' },
        loadChildren: () => import('./address/address.module').then(m => m.AddressModule),
      },
      {
        path: 'phonenumber',
        data: { pageTitle: 'bisterApp.phonenumber.home.title' },
        loadChildren: () => import('./phonenumber/phonenumber.module').then(m => m.PhonenumberModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'bisterApp.notification.home.title' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'enquiry',
        data: { pageTitle: 'bisterApp.enquiry.home.title' },
        loadChildren: () => import('./enquiry/enquiry.module').then(m => m.EnquiryModule),
      },
      {
        path: 'enquiry-response',
        data: { pageTitle: 'bisterApp.enquiryResponse.home.title' },
        loadChildren: () => import('./enquiry-response/enquiry-response.module').then(m => m.EnquiryResponseModule),
      },
      {
        path: 'invoice',
        data: { pageTitle: 'bisterApp.invoice.home.title' },
        loadChildren: () => import('./invoice/invoice.module').then(m => m.InvoiceModule),
      },
      {
        path: 'transaction',
        data: { pageTitle: 'bisterApp.transaction.home.title' },
        loadChildren: () => import('./transaction/transaction.module').then(m => m.TransactionModule),
      },
      {
        path: 'tax-rate',
        data: { pageTitle: 'bisterApp.taxRate.home.title' },
        loadChildren: () => import('./tax-rate/tax-rate.module').then(m => m.TaxRateModule),
      },
      {
        path: 'tax-class',
        data: { pageTitle: 'bisterApp.taxClass.home.title' },
        loadChildren: () => import('./tax-class/tax-class.module').then(m => m.TaxClassModule),
      },
      {
        path: 'refund',
        data: { pageTitle: 'bisterApp.refund.home.title' },
        loadChildren: () => import('./refund/refund.module').then(m => m.RefundModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
