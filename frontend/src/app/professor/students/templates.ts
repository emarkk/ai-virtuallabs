import { TeamStatus } from 'src/app/core/models/team.model';

export const pictureTemplate = student => {
  return `<div class="profile-pic"><img src="${student.picturePath || 'assets/img/user.png'}" /></div>`;
};

export const lastNameTemplate = student => {
  return `${student.lastName.toUpperCase()}`;
};

export const teamTemplate = student => {
  return student.team ? `<span ${student.team.status == TeamStatus.PROVISIONAL ? 'class="team-provisional" title="Provisional team"' : ''}>${student.team.name}</span>` : '&mdash;'
};