export class VmModel {
  id: number;
  name: string;
  configuration: string;

  constructor(id: number, name: string, configuration: string) {
    this.id = id;
    this.name = name;
    this.configuration = configuration;
  }
}