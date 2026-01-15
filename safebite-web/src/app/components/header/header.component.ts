import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthButtonComponent } from '../auth-button/auth-button.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RouterLink, RouterLinkActive, AuthButtonComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  authService = inject(AuthService);
  user$ = this.authService.user$;

  logout() { this.authService.logout(); }
}