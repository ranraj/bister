import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICertification } from '../certification.model';

@Component({
  selector: 'yali-certification-detail',
  templateUrl: './certification-detail.component.html',
})
export class CertificationDetailComponent implements OnInit {
  certification: ICertification | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certification }) => {
      this.certification = certification;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
