import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-button.component.html',
  styleUrl: './auth-button.component.css'
})
export class AuthButtonComponent {
  // Recibe el texto del botón
  @Input() label: string = 'Botón';
  
  // Recibe el tipo: 'primary' (negro) o 'secondary' (blanco)
  @Input() type: 'primary' | 'secondary' = 'primary';
}