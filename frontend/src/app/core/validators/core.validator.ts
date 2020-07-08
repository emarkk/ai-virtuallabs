import { ValidatorFn, AbstractControl } from '@angular/forms';

export const numberValidator: ValidatorFn = (control: AbstractControl) => {
  const input = control.value;
  const ok = /^[0-9]+$/.test(input);
  return ok ? null : { number: true };
};