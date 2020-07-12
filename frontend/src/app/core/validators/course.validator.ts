import { ValidatorFn, FormGroup } from '@angular/forms';

// check that minTeamMembers <= maxTeamMembers
export const newCourseFormValidator: ValidatorFn = (fg: FormGroup) => {
  const minTeamMembers = fg.get('minTeamMembers').value;
  const maxTeamMembers = fg.get('maxTeamMembers').value;
  const ok = fg.get('minTeamMembers').invalid || fg.get('maxTeamMembers').invalid || parseInt(minTeamMembers) <= parseInt(maxTeamMembers);
  return ok ? null : { maxmin: true };
};