import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { User } from '@angular/fire/auth';
import Swal from 'sweetalert2';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  user$: Observable<User | null> = this.authService.user$;

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  async onSubmit() {
    if (this.form.invalid) return;
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
      Swal.fire('Error', 'Usuario o contraseña incorrectos', 'error');
    }
  }
}