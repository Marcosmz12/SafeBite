import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AccessibilityService {
  darkMode = signal(false);
  daltonismo = signal(false);

  toggleDarkMode() {
    this.darkMode.set(!this.darkMode());
    // Aplicamos una clase al body para que afecte a toda la web
    document.body.classList.toggle('dark-mode');
  }

  toggleDaltonismo() {
    this.daltonismo.set(!this.daltonismo());
    // Aplicamos una clase para daltonismo
    document.body.classList.toggle('daltonism-filter');
  }
}