import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  currentLang = signal<'es' | 'en'>('es');

  private dictionary: any = {
    es: {
      recetas: 'Recetas',
      productos: 'Productos',
      subir_receta: 'Subir receta',
      contacto: 'Contacto',
      salir: 'Salir',
      hola: 'Hola,',
      login: 'Inicia Sesión',
      registro: 'Regístrate',

      // TEXTOS DEL HERO
      hero_badge: 'ESPECIALISTAS EN MÁLAGA ☀️',
      hero_titulo: 'Bienvenido a',
      hero_desc: 'Descubre las recetas más reputadas y seguras. Un viaje culinario diseñado para los amantes de la buena mesa en toda España.',
      hero_btn_explorar: 'EXPLORAR RECETAS',
      hero_btn_saber: 'SABER MÁS',

      // Dentro de es: { ... }
      hero_saludo_user: '¡Qué bueno verte de nuevo,',
      hero_invitado: 'Únete a nuestra comunidad',

    },
    en: {
      recetas: 'Recipes',
      productos: 'Products',
      subir_receta: 'Upload',
      contacto: 'Contact',
      salir: 'Logout',
      hola: 'Hello,',
      login: 'Login',
      registro: 'Register',

      // TEXTOS DEL HERO
      hero_badge: 'SPECIALISTS IN MALAGA ☀️',
      hero_titulo: 'Welcome to',
      hero_desc: 'Discover the most reputable and safe recipes. A culinary journey designed for food lovers across Spain.',
      hero_btn_explorar: 'EXPLORE RECIPES',
      hero_btn_saber: 'LEARN MORE',

      // Dentro de en: { ... }
      hero_saludo_user: 'Great to see you again,',
      hero_invitado: 'Join our community',
    }
  };

  t = computed(() => this.dictionary[this.currentLang()]);

  setLanguage(lang: 'es' | 'en') {
    this.currentLang.set(lang);
  }
}