import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TaxRateDetailComponent } from './tax-rate-detail.component';

describe('TaxRate Management Detail Component', () => {
  let comp: TaxRateDetailComponent;
  let fixture: ComponentFixture<TaxRateDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaxRateDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ taxRate: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TaxRateDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TaxRateDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load taxRate on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.taxRate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
