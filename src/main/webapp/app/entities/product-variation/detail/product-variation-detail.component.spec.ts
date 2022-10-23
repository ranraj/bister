import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductVariationDetailComponent } from './product-variation-detail.component';

describe('ProductVariation Management Detail Component', () => {
  let comp: ProductVariationDetailComponent;
  let fixture: ComponentFixture<ProductVariationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductVariationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productVariation: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductVariationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductVariationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productVariation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productVariation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
