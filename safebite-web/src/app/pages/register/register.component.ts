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
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
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
    console.log('Captcha resuelto con éxito para el registro');
  }

  async onSubmit() {
    // 3. Validar el formulario de registro
    if (this.form.invalid) return;

    // 4. BLOQUEO: Validar que el captcha esté hecho
    if (!this.captchaToken) {
      Swal.fire({
        title: 'Verificación necesaria',
        text: 'Por favor, completa el captcha para crear tu cuenta.',
        icon: 'info',
        confirmButtonColor: '#4CAF50'
      });
      return;
    }

    const { email, password } = this.form.getRawValue();

    try {
      // 5. Intentar crear la cuenta en Firebase
      await this.authService.register(email, password);
      
      Swal.fire({
        title: '¡Éxito!',
        text: 'Cuenta creada correctamente. ¡Bienvenido a SafeBite!',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      });

      this.router.navigateByUrl('/recetas');
      
    } catch (e) {
      // Si falla (ej: el email ya existe), reseteamos el token
      this.captchaToken = null;
      
      Swal.fire('Error', 'No se ha podido crear la cuenta. Es posible que el correo ya esté en uso.', 'error');
      console.error(e);
    }
  }
}