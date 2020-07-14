export class ToastData {
  type?: ToastType;
  text: string;
}

export type ToastType = 'warning' | 'info' | 'success' | 'danger';