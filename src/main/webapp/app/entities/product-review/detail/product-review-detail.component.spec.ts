import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductReviewDetailComponent } from './product-review-detail.component';

describe('ProductReview Management Detail Component', () => {
  let comp: ProductReviewDetailComponent;
  let fixture: ComponentFixture<ProductReviewDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductReviewDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productReview: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductReviewDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductReviewDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productReview on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productReview).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
