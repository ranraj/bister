import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductActivityDetailComponent } from './product-activity-detail.component';

describe('ProductActivity Management Detail Component', () => {
  let comp: ProductActivityDetailComponent;
  let fixture: ComponentFixture<ProductActivityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductActivityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productActivity: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductActivityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductActivityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productActivity on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productActivity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
