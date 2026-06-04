import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { User } from '@angular/fire/auth';
import Swal from 'sweetalert2';
import { RecaptchaModule } from 'ng-recaptcha'; // Importación correcta

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, RecaptchaModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // --- NUEVA VARIABLE PARA LA VISIBILIDAD ---
  hidePassword = true;

  captchaToken: string | null = null;
  user$: Observable<User | null> = this.authService.user$;

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  // --- NUEVO MÉTODO ---
  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  onResolved(token: string | null) {
    this.captchaToken = token;
  }

  async onSubmit() {
    if (this.form.invalid) return;
    if (!this.captchaToken) {
      Swal.fire({
        title: 'Atención',
        text: 'Por favor, confirma que no eres un robot.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    const { email, password } = this.form.getRawValue();

    try {
      await this.authService.login(email, password);
      Swal.fire({
        title: '¡Bienvenido!',
        text: 'Inicio de sesión correcto',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      });
      this.router.navigateByUrl('/recetas');
    } catch (e) {
      this.captchaToken = null;
      Swal.fire('Error', 'Usuario o contraseña incorrectos', 'error');
    }
  }
}