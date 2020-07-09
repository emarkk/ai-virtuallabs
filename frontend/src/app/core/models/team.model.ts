export class Team {
  id: number;
  name: string;
  status: number;

  constructor(id: number, name: string, status: number) {
    this.id = id;
    this.name = name;
    this.status = status;
  }
}