import { OverlayRef } from '@angular/cdk/overlay';

export class ToastRef {

  constructor(readonly overlay: OverlayRef) {
  }
  
  close() {
    this.overlay.dispose();
  }

}