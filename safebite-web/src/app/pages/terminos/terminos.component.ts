import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-terminos',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './terminos.component.html',
  styleUrls: ['./legal.shared.css']
})
export class TerminosComponent {}
