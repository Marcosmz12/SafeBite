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

  // 1. Variable para guardar el token del captcha
  captchaToken: string | null = null;

  user$: Observable<User | null> = this.authService.user$;

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  // 2. Función que se ejecuta cuando el usuario resuelve el captcha
  onResolved(token: string | null) {
    this.captchaToken = token;
    console.log('Captcha resuelto con éxito');
  }

  async onSubmit() {
    // 3. Validar el formulario primero
    if (this.form.invalid) return;

    // 4. Validar que el captcha se haya completado
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
      // Si el login falla, es recomendable resetear el captcha para que el usuario lo vuelva a marcar
      // Nota: Para resetearlo visualmente necesitarías una referencia al ViewChild, 
      // pero por ahora limpiar el token bastará para la lógica.
      this.captchaToken = null; 
      Swal.fire('Error', 'Usuario o contraseña incorrectos', 'error');
    }
  }
}