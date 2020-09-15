import { Component, OnInit, Input } from '@angular/core';

import { Professor } from 'src/app/core/models/professor.model';
import { Student } from 'src/app/core/models/student.model';

import { ImageService } from 'src/app/core/services/image.service';

@Component({
  selector: 'app-secure-image',
  templateUrl: './secure-image.component.html',
  styleUrls: ['./secure-image.component.css']
})
export class SecureImageComponent implements OnInit {
  imageSrc: string = null;

  @Input() set user(value: Student|Professor) {
    if(value instanceof Student && value.hasPicture)
      this.imageService.getStudent(value.id).subscribe(src => this.imageSrc = src);
    else if(value instanceof Professor && value.hasPicture)
      this.imageService.getProfessor(value.id).subscribe(src => this.imageSrc = src);
  }

  constructor(private imageService: ImageService) {
  }

  ngOnInit(): void {
  }

}