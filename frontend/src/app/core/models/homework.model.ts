export class Homework {
  id: number;
  title: string;
  publicationDate: Date;
  dueDate: Date;
  link: string;

  constructor(id: number, title: string, publicationDate: Date, dueDate: Date, link?: string) {
    this.id = id;
    this.title = title;
    this.publicationDate = publicationDate;
    this.dueDate = dueDate;
    this.link = link;
  }
}