import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { Observable } from 'rxjs'; // Simplificado el import
import { RecetasService } from '../../services/recetas.service';
import { Receta } from '../../models/receta';
import { RecipeSearchComponent } from '../../components/recipe-search/recipe-search.component';

@Component({
  selector: 'app-recetas',
  standalone: true,
  // 1. Añadimos el componente de búsqueda aquí
  imports: [CommonModule, AsyncPipe, RecipeCardComponent, RecipeSearchComponent], 
  templateUrl: './recetas.component.html',
  styleUrl: './recetas.component.css'
})
export class RecetasComponent implements OnInit {
  private recetasService = inject(RecetasService);
  
  // Centralizamos los filtros en una sola variable
  filtrosActuales = { texto: '', alergenos: [] as string[] };
  
  recetas$!: Observable<Receta[]>;
  categorias = ['Entrantes', 'Platos Principales', 'Postres'];

  ngOnInit() {
    this.recetas$ = this.recetasService.getRecetas();
  }

  // 2. Esta función recibe los datos del componente buscador
  aplicarFiltros(event: {texto: string, alergenos: string[]}) {
    this.filtrosActuales = event;
  }

  // 3. Función principal de filtrado (Título + Alérgenos)
  obtenerRecetasFiltradas(recetas: Receta[]): Receta[] {
    if (!recetas) return [];

    return recetas.filter(r => {
      // Filtro por texto
      const cumpleTexto = r.titulo.toLowerCase().includes(this.filtrosActuales.texto);
      
      // Filtro por alérgenos (Debe cumplir todos los seleccionados)
      const cumpleAlergenos = this.filtrosActuales.alergenos.every(tag => 
        r.etiquetas_sin?.map(e => e.toLowerCase()).includes(tag.toLowerCase())
      );

      return cumpleTexto && cumpleAlergenos;
    });
  }

  // 4. Función auxiliar para organizar por categorías en el HTML
  filtrarPorCategoria(recetas: Receta[], categoria: string): Receta[] {
    return recetas.filter(r => r.categoria === categoria);
  }
}