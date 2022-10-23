import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnquiryDetailComponent } from './enquiry-detail.component';

describe('Enquiry Management Detail Component', () => {
  let comp: EnquiryDetailComponent;
  let fixture: ComponentFixture<EnquiryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnquiryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ enquiry: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnquiryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnquiryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load enquiry on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.enquiry).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
