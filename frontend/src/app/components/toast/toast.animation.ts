// heavily inspired by
// https://medium.com/angular-in-depth/creating-a-toast-service-with-angular-cdk-a0d35fd8cc12

import {
  AnimationTriggerMetadata,
  trigger,
  state,
  transition,
  style,
  animate,
} from '@angular/animations';

export const toastAnimations: {
  readonly fadeToast: AnimationTriggerMetadata;
} = {
  fadeToast: trigger('fadeAnimation', [
    state('default', style({ opacity: 1 })),
    transition('void => *', [style({ opacity: 0 }), animate('{{ fadeIn }}ms')]),
    transition(
      'default => closing',
      animate('{{ fadeOut }}ms', style({ opacity: 0 })),
    ),
  ]),
};

export type ToastAnimationState = 'default' | 'closing';
