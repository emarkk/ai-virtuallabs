export class Course {
  code: string;
  name: string;
  acronym: string;
  minTeamMembers: number;
  maxTeamMembers: number;
  enabled: boolean;

  constructor(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean) {
    this.code = code;
    this.name = name;
    this.acronym = acronym;
    this.minTeamMembers = minTeamMembers;
    this.maxTeamMembers = maxTeamMembers;
    this.enabled = enabled;
  }
}