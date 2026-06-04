import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { Observable } from 'rxjs';
import { RecetasService } from '../../services/recetas.service';
import { Receta } from '../../models/receta';
import { RecipeSearchComponent } from '../../components/recipe-search/recipe-search.component';
import { LanguageService } from '../../services/language.service'; 

@Component({
  selector: 'app-recetas',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RecipeCardComponent, RecipeSearchComponent], 
  templateUrl: './recetas.component.html',
  styleUrl: './recetas.component.css'
})
export class RecetasComponent implements OnInit {
  private recetasService = inject(RecetasService);
  // 2. INYECTAMOS EL SERVICIO (público para que el HTML lo vea)
  public langService = inject(LanguageService); 
  
  filtrosActuales = { texto: '', alergenos: [] as string[] };
  recetas$!: Observable<Receta[]>;

  // Las categorías las mantenemos como "llaves" para que el filtro no se rompa
  // pero usaremos el servicio para mostrarlas en el idioma correcto
  categorias = ['Entrantes', 'Platos Principales', 'Postres'];

  ngOnInit() {
    this.recetas$ = this.recetasService.getRecetas();
  }

  aplicarFiltros(event: {texto: string, alergenos: string[]}) {
    this.filtrosActuales = event;
  }

  obtenerRecetasFiltradas(recetas: Receta[]): Receta[] {
    if (!recetas) return [];
    return recetas.filter(r => {
      const cumpleTexto = r.titulo.toLowerCase().includes(this.filtrosActuales.texto);
      const cumpleAlergenos = this.filtrosActuales.alergenos.every(tag => 
        r.etiquetas_sin?.map(e => e.toLowerCase()).includes(tag.toLowerCase())
      );
      return cumpleTexto && cumpleAlergenos;
    });
  }

  filtrarPorCategoria(recetas: Receta[], categoria: string): Receta[] {
    return recetas.filter(r => r.categoria === categoria);
  }

  // 3. FUNCIÓN PARA TRADUCIR CATEGORÍAS DINÁMICAMENTE
  getNombreCategoria(cat: string): string {
    const traducciones: any = {
      'Entrantes': this.langService.t().cat_entrantes,
      'Platos Principales': this.langService.t().cat_principales,
      'Postres': this.langService.t().cat_postres
    };
    return traducciones[cat] || cat;
  }
}