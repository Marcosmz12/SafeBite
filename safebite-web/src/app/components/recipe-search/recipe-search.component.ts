import { Component, EventEmitter, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PerfilService } from '../../services/perfil.service';
import { LanguageService } from '../../services/language.service';
import { switchMap, of } from 'rxjs';

@Component({
  selector: 'app-recipe-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recipe-search.component.html',
  styleUrl: './recipe-search.component.css'
})
export class RecipeSearchComponent implements OnInit {
  @Output() filtrosCambiados = new EventEmitter<{texto: string, alergenos: string[]}>();

  private authService = inject(AuthService);
  private perfilService = inject(PerfilService);
  public langService = inject(LanguageService);

  textoBusqueda: string = '';
  alergenosSeleccionados: string[] = [];

  // 1. Alérgenos básicos que siempre salen
  alergenosBase = [
    { id: 'gluten', nombre: 'Sin Gluten', icono: '🌾' },
    { id: 'lactosa', nombre: 'Sin Lactosa', icono: '🥛' },
    { id: 'huevo', nombre: 'Sin Huevo', icono: '🥚' }
  ];

  // 2. Aquí guardaremos la mezcla de básicos + personalizados del usuario
  filtrosDisponibles: any[] = [];

  ngOnInit() {
    // Cargamos los filtros base por defecto
    this.filtrosDisponibles = [...this.alergenosBase];

    // Escuchamos si hay un usuario logueado para traer sus alergias personalizadas
    this.authService.user$.pipe(
      switchMap(user => user ? this.perfilService.getPerfil(user.uid) : of(null))
    ).subscribe(perfil => {
      if (perfil && perfil.alergias) {
        this.combinarAlergenos(perfil.alergias);
      }
    });
  }

  private combinarAlergenos(alergiasUsuario: string[]) {
    // Empezamos con los base
    const nuevosFiltros = [...this.alergenosBase];

    alergiasUsuario.forEach(alergia => {
      const idLimpio = alergia.toLowerCase().trim();
      // Si la alergia del perfil no está ya en los básicos, la añadimos
      if (!nuevosFiltros.some(f => f.id === idLimpio)) {
        nuevosFiltros.push({
          id: idLimpio,
          nombre: alergia,
          icono: '⭐' // Icono especial para las personalizadas
        });
      }
    });

    this.filtrosDisponibles = nuevosFiltros;
  }

  // --- FUNCIÓN DE TRADUCCIÓN INTELIGENTE ---
  getTraduccionFiltro(id: string, nombreOriginal: string): string {
    const t = this.langService.t();
    
    // 1. Si es uno de los básicos, usamos la traducción del diccionario
    if (id === 'gluten') return t.filter_gluten;
    if (id === 'lactosa') return t.filter_lactose;
    if (id === 'huevo') return t.filter_egg;

    // 2. Si es una alergia personalizada del usuario (ej: "soja")
    // Ponemos "Sin " o "No " delante según el idioma
    const prefijo = this.langService.currentLang() === 'es' ? 'Sin ' : 'No ';
    return prefijo + nombreOriginal;
  }

  toggleAlergeno(id: string) {
    if (this.alergenosSeleccionados.includes(id)) {
      this.alergenosSeleccionados = this.alergenosSeleccionados.filter(a => a !== id);
    } else {
      this.alergenosSeleccionados.push(id);
    }
    this.notificarCambios();
  }

  actualizarBusqueda() { this.notificarCambios(); }

  private notificarCambios() {
    this.filtrosCambiados.emit({
      texto: this.textoBusqueda.toLowerCase(),
      alergenos: this.alergenosSeleccionados
    });
  }
}