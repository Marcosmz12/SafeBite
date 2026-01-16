import { Component, EventEmitter, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PerfilService } from '../../services/perfil.service';
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
          nombre: 'Sin ' + alergia,
          icono: '⭐' // Icono especial para las personalizadas
        });
      }
    });

    this.filtrosDisponibles = nuevosFiltros;
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