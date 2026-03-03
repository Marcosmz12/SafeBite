import { Component, inject, OnInit, NgZone } from '@angular/core'; // Añadimos OnInit y NgZone
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { User } from '@angular/fire/auth';
import Swal from 'sweetalert2';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit { // Agregamos el implements
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private ngZone = inject(NgZone); // Inyectamos NgZone

  captchaToken: string | null = null;
  user$: Observable<User | null> = this.authService.user$;

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit() {
    // IMPORTANTE: Definimos la función que el HTML está buscando (onRecaptchaSuccess)
    (window as any).onRecaptchaSuccess = (token: string) => {
      // Usamos ngZone para que Angular se entere del cambio de valor
      this.ngZone.run(() => {
        this.captchaToken = token;
        console.log('Captcha resuelto con éxito');
      });
    };
  }

  async onSubmit() {
    // 1. Validar formulario
    if (this.form.invalid) return;

    // 2. Validar que tenemos el token (que ya se guardó automáticamente por el callback)
    if (!this.captchaToken) {
      Swal.fire('Atención', 'Por favor, completa el reCAPTCHA', 'warning');
      return;
    }

    const { email, password } = this.form.getRawValue();

    try {
      // 3. Intentar registro
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
      // 4. Si falla, reseteamos el captcha visual y el token
      this.captchaToken = null;
      if ((window as any).grecaptcha) {
        (window as any).grecaptcha.reset();
      }

      Swal.fire('Error', 'No se ha podido crear la cuenta. Es posible que el correo ya esté en uso.', 'error');
      console.error(e);
    }
  }
}