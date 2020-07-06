import { ValidatorFn, AbstractControl, FormGroup } from '@angular/forms';

const professorEmailRegex = /^(d\d{5}|[a-z]+\.[a-z]+)@polito\.it$/i;
const studentEmailRegex = /^(s\d{6}|[a-z]+\.[a-z]+)@studenti\.polito\.it$/i;

const professorMatricolaRegex = /^d\d{5}$/i;
const studentMatricolaRegex = /^s\d{6}$/i;

const checkEmailMatricolaPair = (email, matricola) => {
  const [emailUser, emailDomain] = email.split('@');
  const domainOk = professorMatricolaRegex.test(matricola) ?
      emailDomain === 'polito.it' : emailDomain === 'studenti.polito.it';

  if(!domainOk)
    return false;

  if(/^[ds]\d+$/i.test(emailUser) && emailUser.toLowerCase() != matricola.toLowerCase())
    return false;

  return true;
};

export const politoEmailValidator: ValidatorFn = (control: AbstractControl) => {
  const input = control.value;
  const ok = professorEmailRegex.test(input) || studentEmailRegex.test(input);
  return ok ? null : { email: true };
};
export const politoMatricolaValidator: ValidatorFn = (control: AbstractControl) => {
  const input = control.value;
  const ok = professorMatricolaRegex.test(input) || studentMatricolaRegex.test(input);
  return ok ? null : { matricola: true };
};
export const politoUsernameValidator: ValidatorFn = (control: AbstractControl) => {
  const input = control.value;
  const ok = professorMatricolaRegex.test(input) || studentMatricolaRegex.test(input) ||
      professorEmailRegex.test(input) || studentEmailRegex.test(input);
  return ok ? null : { username: true };
};
export const politoSignUpFormValidator: ValidatorFn = (fg: FormGroup) => {
  const email = fg.get('email').value;
  const matricola = fg.get('matricola').value;
  const ok = fg.get('email').invalid || fg.get('matricola').invalid || checkEmailMatricolaPair(email, matricola);
  return ok ? null : { conflict: true };
};