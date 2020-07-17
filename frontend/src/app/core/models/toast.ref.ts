// heavily inspired by
// https://medium.com/angular-in-depth/creating-a-toast-service-with-angular-cdk-a0d35fd8cc12

import { OverlayRef } from '@angular/cdk/overlay';

export class ToastRef {

  constructor(readonly overlay: OverlayRef) {
  }
  
  close() {
    this.overlay.dispose();
  }

}