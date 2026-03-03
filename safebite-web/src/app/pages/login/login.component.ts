import { Component, inject, OnInit, NgZone } from '@angular/core'; // 1. Importar OnInit y NgZone
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
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit { // 2. Implementar OnInit
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private ngZone = inject(NgZone); // 3. Inyectar NgZone

  hidePassword = true;
  captchaToken: string | null = null; // Aquí guardaremos el token

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit() {
    // 4. Conectamos el callback del HTML con Angular
    (window as any).onRecaptchaSuccess = (token: string) => {
      this.ngZone.run(() => {
        this.captchaToken = token;
        console.log('✅ Captcha resuelto correctamente');
      });
    };
  }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  async onSubmit() {
    // 5. YA NO USAMOS window.grecaptcha.getResponse() (eso evitara el error)
    if (!this.captchaToken) {
      Swal.fire('Atención', 'Por favor, completa el reCAPTCHA', 'warning');
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
      // 6. Si falla, reseteamos el captcha
      this.captchaToken = null;
      if ((window as any).grecaptcha) (window as any).grecaptcha.reset();
      
      Swal.fire('Error', 'Usuario o contraseña incorrectos', 'error');
    }
  }
}