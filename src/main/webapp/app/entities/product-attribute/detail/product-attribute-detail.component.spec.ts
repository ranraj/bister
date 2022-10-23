import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductAttributeDetailComponent } from './product-attribute-detail.component';

describe('ProductAttribute Management Detail Component', () => {
  let comp: ProductAttributeDetailComponent;
  let fixture: ComponentFixture<ProductAttributeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductAttributeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productAttribute: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductAttributeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductAttributeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productAttribute on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productAttribute).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
