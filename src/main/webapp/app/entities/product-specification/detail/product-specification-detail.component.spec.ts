import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductSpecificationDetailComponent } from './product-specification-detail.component';

describe('ProductSpecification Management Detail Component', () => {
  let comp: ProductSpecificationDetailComponent;
  let fixture: ComponentFixture<ProductSpecificationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductSpecificationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productSpecification: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductSpecificationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductSpecificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productSpecification on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productSpecification).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
