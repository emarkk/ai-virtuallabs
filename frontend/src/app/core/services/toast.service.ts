// heavily inspired by
// https://medium.com/angular-in-depth/creating-a-toast-service-with-angular-cdk-a0d35fd8cc12

import { Injectable, Injector } from '@angular/core';
import { Overlay } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';

import { ToastRef } from '../models/toast.ref';
import { ToastData } from '../models/toast-data.model';

import { ToastComponent } from 'src/app/components/toast/toast.component';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor(private overlay: Overlay, private parentInjector: Injector) {
  }
  
  show(data: ToastData) {
    const overlayRef = this.overlay.create();
    const toastRef = new ToastRef(overlayRef);
    const injector = this.getInjector(data, toastRef, this.parentInjector);
    const toastPortal = new ComponentPortal(ToastComponent, null, injector);
    overlayRef.attach(toastPortal);
  }

  getInjector(data: ToastData, toastRef: ToastRef, parentInjector: Injector) {
    const tokens = new WeakMap();
    tokens.set(ToastData, data);
    tokens.set(ToastRef, toastRef);
    return new PortalInjector(this.parentInjector, tokens);
  }
  
}