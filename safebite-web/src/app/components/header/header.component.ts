import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service'; // <--- 1. Importar
import { AuthButtonComponent } from '../auth-button/auth-button.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RouterLink, RouterLinkActive, AuthButtonComponent],
  templateUrl: './header.component.html', // <--- ASEGÚRATE que ponga header.component.html
  styleUrl: './header.component.css'
})
export class HeaderComponent { // <--- CAMBIA 'HeroComponent' por 'HeaderComponent'
  authService = inject(AuthService);
  langService = inject(LanguageService);
  user$ = this.authService.user$;

  logout() { this.authService.logout(); }
}