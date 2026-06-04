import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service'; // <--- Importante
import { AuthButtonComponent } from '../../components/auth-button/auth-button.component';
@Component({
  selector: 'app-hero', // <--- Asegúrate que diga app-hero
  standalone: true,
  imports: [CommonModule, AsyncPipe, RouterLink, RouterLinkActive, AuthButtonComponent],
  templateUrl: './hero.component.html', // <--- Que apunte a su html
  styleUrl: './hero.component.css'
})
export class HeroComponent {
  authService = inject(AuthService);
  langService = inject(LanguageService); // <--- Inyectamos el servicio
  user$ = this.authService.user$;

  logout() { this.authService.logout(); }
}