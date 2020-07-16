// heavily inspired by
// https://medium.com/angular-in-depth/creating-a-toast-service-with-angular-cdk-a0d35fd8cc12

import { Component, OnInit } from '@angular/core';
import { AnimationEvent } from '@angular/animations';

import { ToastRef } from 'src/app/core/models/toast.ref';
import { ToastData } from 'src/app/core/models/toast-data.model';

import { ToastAnimationState, toastAnimations } from './toast.animation';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  animations: [toastAnimations.fadeToast]
})
export class ToastComponent implements OnInit {
  animationState: ToastAnimationState = 'default';

  private intervalId: number;

  constructor(readonly data: ToastData, readonly ref: ToastRef) {
  }

  ngOnInit(): void {
    this.intervalId = window.setTimeout(() => this.animationState = 'closing', 2000);
  }

  ngOnDestroy(): void {
    clearTimeout(this.intervalId);
  }

  close() {
    this.ref.close();
  }

  onFadeFinished(event: AnimationEvent) {
    const { toState } = event;
    const isFadeOut = (toState as ToastAnimationState) === 'closing';
    const itFinished = this.animationState === 'closing';

    if(isFadeOut && itFinished) {
      this.close();
    }
  }

}