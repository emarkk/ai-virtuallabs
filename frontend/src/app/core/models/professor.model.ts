export class Professor {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  picturePath: string;

  constructor(id: number, firstName: string, lastName: string, email: string, picturePath: string) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.picturePath = picturePath;
  }
}