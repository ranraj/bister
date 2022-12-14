import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAttachment } from '../attachment.model';

@Component({
  selector: 'yali-attachment-detail',
  templateUrl: './attachment-detail.component.html',
})
export class AttachmentDetailComponent implements OnInit {
  attachment: IAttachment | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attachment }) => {
      this.attachment = attachment;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
