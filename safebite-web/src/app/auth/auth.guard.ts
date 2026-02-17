import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth, authState } from '@angular/fire/auth';
import { map, take } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  // Inyectamos las herramientas necesarias
  const auth = inject(Auth);
  const router = inject(Router);

  // Verificamos si el usuario está autenticado en Firebase
  return authState(auth).pipe(
    take(1), // Importante: toma la primera respuesta y termina
    map(user => {
      if (user) {
        // SI hay usuario: permitimos el acceso a la página
        return true; 
      } else {
        // NO hay usuario: lanzamos aviso y mandamos al login
        alert('Lo sentimos, debes estar registrado para subir recetas 🔒');
        router.navigate(['/login']);
        return false;
      }
    })
  );
};