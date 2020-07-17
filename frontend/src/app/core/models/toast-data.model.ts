// heavily inspired by
// https://medium.com/angular-in-depth/creating-a-toast-service-with-angular-cdk-a0d35fd8cc12

export class ToastData {
  type?: ToastType;
  text: string;
}

export type ToastType = 'warning' | 'info' | 'success' | 'danger';