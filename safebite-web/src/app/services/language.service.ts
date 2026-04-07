import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  currentLang = signal<'es' | 'en'>('es');

  private dictionary: any = {
    es: {
      // Navbar
        recetas: 'Recetas', productos: 'Productos', subir_receta: 'Subir receta', contacto: 'Contacto',
        salir: 'Salir', hola: 'Hola,', login: 'Inicia Sesión', registro: 'Regístrate',
        // Hero
        hero_badge: 'Especialistas en Málaga ☀️', hero_titulo: 'Bienvenido a',
        hero_desc: 'Descubre las recetas más reputadas y seguras.',
        hero_saludo_user: '¡Qué bueno verte de nuevo,', hero_invitado: 'Únete a nuestra comunidad',
        // Página Recetas
        rec_titulo: 'Nuestro Catálogo de Recetas', rec_buscar: 'Busca una receta...',
        rec_filtros: 'FILTROS INTELIGENTES:', rec_no_resultados: 'No se encontraron recetas',
        // Página Productos (Supermercados)
        sup_titulo: '¿En qué Supermercado vas a buscar?', sup_desc: 'Analizaremos los productos según tu perfil de salud.',
        sup_analizar: 'Analizar',
        // Página Subir Receta
        sub_nueva: 'Nueva Receta', sub_foto: 'Subir foto de la receta', sub_t_receta: 'Título de la receta',
        sub_cat: 'Categoría', sub_ing: 'Ingredientes', sub_pasos: 'Pasos de preparación',
        sub_etiquetas: 'Etiquetas "Sin":', sub_publicar: 'Publicar Receta',
        // Página Contacto
        con_titulo: 'Contacta con nosotros',
        con_subtitulo: 'Estamos para ayudarte 🍃',
        con_desc: 'En SafeBite nos tomamos muy en serio la seguridad alimentaria. Escríbenos si tienes dudas sobre alérgenos, recetas o el uso de la plataforma.',
        con_feat1: 'Respuesta en menos de 24h',
        con_feat2: 'Asesoramiento sobre alérgenos',
        con_feat3: 'Soporte humano y cercano',
        con_form_header: 'Contacto SafeBite',
        con_form_sub: 'Te ayudaremos en menos de 24h.',
        con_label_nombre: 'Nombre completo',
        con_placeholder_nombre: 'Ej. Juan Pérez',
        con_label_email: 'Correo electrónico',
        con_label_mensaje: 'Tu mensaje',
        con_placeholder_mensaje: 'Cuéntanos detalladamente tu consulta...',
        con_btn_enviar: 'Enviar Mensaje',
        con_privacidad: '🔒 Tus datos están protegidos y no se compartirán con terceros.'
      },
      en: {
        // Navbar
        recetas: 'Recipes', productos: 'Products', subir_receta: 'Upload', contacto: 'Contact',
        salir: 'Logout', hola: 'Hello,', login: 'Login', registro: 'Register',
        // Hero
        hero_badge: 'Specialists in Malaga ☀️', hero_titulo: 'Welcome to',
        hero_desc: 'Discover the most reputable and safe recipes.',
        hero_saludo_user: 'Great to see you again,', hero_invitado: 'Join our community',
        // Página Recetas
        rec_titulo: 'Our Recipe Catalog', rec_buscar: 'Search for a recipe...',
        rec_filtros: 'SMART FILTERS:', rec_no_resultados: 'No recipes found',
        // Página Productos (Supermercados)
        sup_titulo: 'Which Supermarket are you searching in?', sup_desc: 'We analyze products based on your health profile.',
        sup_analizar: 'Analyze',
        // Página Subir Receta
        sub_nueva: 'New Recipe', sub_foto: 'Upload recipe photo', sub_t_receta: 'Recipe Title',
        sub_cat: 'Category', sub_ing: 'Ingredients', sub_pasos: 'Preparation Steps',
        sub_etiquetas: 'Allergen-free Tags:', sub_publicar: 'Publish Recipe',
        // Página Contacto
        con_titulo: 'Contact us',
        con_subtitulo: 'We are here to help 🍃',
        con_desc: 'At SafeBite, we take food safety very seriously. Write to us if you have questions about allergens, recipes, or using the platform.',
        con_feat1: 'Response in less than 24h',
        con_feat2: 'Allergen advice',
        con_feat3: 'Personal and friendly support',
        con_form_header: 'SafeBite Contact',
        con_form_sub: 'We will help you within 24h.',
        con_label_nombre: 'Full name',
        con_placeholder_nombre: 'e.g. John Doe',
        con_label_email: 'Email address',
        con_label_mensaje: 'Your message',
        con_placeholder_mensaje: 'Tell us about your inquiry in detail...',
        con_btn_enviar: 'Send Message',
        con_privacidad: '🔒 Your data is protected and will not be shared with third parties.'
    }
  };

  t = computed(() => this.dictionary[this.currentLang()]);

  setLanguage(lang: 'es' | 'en') {
    this.currentLang.set(lang);
  }
}