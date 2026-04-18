import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AccessibilityService {
  darkMode = signal(localStorage.getItem('theme') === 'dark');
  daltonismo = signal(localStorage.getItem('daltonismo') === 'true');
  largeText = signal(localStorage.getItem('largeText') === 'true');

  constructor() {
    // Aplicar preferencias guardadas al cargar la web
    if (this.darkMode()) document.body.classList.add('dark-mode');
    if (this.daltonismo()) document.body.classList.add('daltonism-filter');
    if (this.largeText()) document.body.classList.add('large-text');
  }

  toggleDarkMode() {
    this.darkMode.set(!this.darkMode());
    const isDark = document.body.classList.toggle('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
  }

  toggleDaltonismo() {
    this.daltonismo.set(!this.daltonismo());
    const isDalton = document.body.classList.toggle('daltonism-filter');
    localStorage.setItem('daltonismo', isDalton ? 'true' : 'false');
  }

  toggleLargeText() {
    this.largeText.set(!this.largeText());
    const isLarge = document.body.classList.toggle('large-text');
    localStorage.setItem('largeText', isLarge ? 'true' : 'false');
  }
}