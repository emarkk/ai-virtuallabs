export class Course {
  code: string;
  name: string;
  acronym: string;
  minTeamMembers: number;
  maxTeamMembers: number;
  enabled: boolean;
  link: string;

  constructor(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean, link?: string) {
    this.code = code;
    this.name = name;
    this.acronym = acronym;
    this.minTeamMembers = minTeamMembers;
    this.maxTeamMembers = maxTeamMembers;
    this.enabled = enabled;
    this.link = link;
  }
}