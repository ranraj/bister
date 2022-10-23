import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PhonenumberDetailComponent } from './phonenumber-detail.component';

describe('Phonenumber Management Detail Component', () => {
  let comp: PhonenumberDetailComponent;
  let fixture: ComponentFixture<PhonenumberDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PhonenumberDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ phonenumber: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PhonenumberDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PhonenumberDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load phonenumber on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.phonenumber).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
