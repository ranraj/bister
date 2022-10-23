import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BookingOrderDetailComponent } from './booking-order-detail.component';

describe('BookingOrder Management Detail Component', () => {
  let comp: BookingOrderDetailComponent;
  let fixture: ComponentFixture<BookingOrderDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BookingOrderDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ bookingOrder: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BookingOrderDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BookingOrderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookingOrder on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.bookingOrder).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
