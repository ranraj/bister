import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductVariationAttributeTermDetailComponent } from './product-variation-attribute-term-detail.component';

describe('ProductVariationAttributeTerm Management Detail Component', () => {
  let comp: ProductVariationAttributeTermDetailComponent;
  let fixture: ComponentFixture<ProductVariationAttributeTermDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductVariationAttributeTermDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productVariationAttributeTerm: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductVariationAttributeTermDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductVariationAttributeTermDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productVariationAttributeTerm on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productVariationAttributeTerm).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
