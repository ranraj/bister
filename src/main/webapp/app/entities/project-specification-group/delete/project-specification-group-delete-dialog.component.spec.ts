jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ProjectSpecificationGroupService } from '../service/project-specification-group.service';

import { ProjectSpecificationGroupDeleteDialogComponent } from './project-specification-group-delete-dialog.component';

describe('ProjectSpecificationGroup Management Delete Component', () => {
  let comp: ProjectSpecificationGroupDeleteDialogComponent;
  let fixture: ComponentFixture<ProjectSpecificationGroupDeleteDialogComponent>;
  let service: ProjectSpecificationGroupService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProjectSpecificationGroupDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(ProjectSpecificationGroupDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectSpecificationGroupDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProjectSpecificationGroupService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
