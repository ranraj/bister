import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PaymentScheduleDetailComponent } from './payment-schedule-detail.component';

describe('PaymentSchedule Management Detail Component', () => {
  let comp: PaymentScheduleDetailComponent;
  let fixture: ComponentFixture<PaymentScheduleDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentScheduleDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ paymentSchedule: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PaymentScheduleDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PaymentScheduleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load paymentSchedule on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.paymentSchedule).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
