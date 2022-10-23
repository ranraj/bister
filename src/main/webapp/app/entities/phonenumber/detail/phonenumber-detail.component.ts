import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPhonenumber } from '../phonenumber.model';

@Component({
  selector: 'yali-phonenumber-detail',
  templateUrl: './phonenumber-detail.component.html',
})
export class PhonenumberDetailComponent implements OnInit {
  phonenumber: IPhonenumber | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ phonenumber }) => {
      this.phonenumber = phonenumber;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
