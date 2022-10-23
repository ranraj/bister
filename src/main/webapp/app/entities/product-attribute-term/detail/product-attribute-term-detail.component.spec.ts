import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductAttributeTermDetailComponent } from './product-attribute-term-detail.component';

describe('ProductAttributeTerm Management Detail Component', () => {
  let comp: ProductAttributeTermDetailComponent;
  let fixture: ComponentFixture<ProductAttributeTermDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductAttributeTermDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productAttributeTerm: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductAttributeTermDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductAttributeTermDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productAttributeTerm on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productAttributeTerm).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
