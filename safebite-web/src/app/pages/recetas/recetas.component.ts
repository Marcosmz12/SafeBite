import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { Observable } from 'rxjs/internal/Observable';
import { RecetasService } from '../../services/recetas.service';
import { Receta } from '../../models/receta';



@Component({
  selector: 'app-recetas',
  standalone: true,
  // ESTO ES CLAVE: Añade los componentes aquí
  imports: [CommonModule, AsyncPipe, RecipeCardComponent], 
  templateUrl: './recetas.component.html',
  styleUrl: './recetas.component.css'
})
export class RecetasComponent implements OnInit {
  private recetasService = inject(RecetasService);
  
  recetas$!: Observable<Receta[]>;
  categorias = ['Entrantes', 'Platos Principales', 'Postres'];

  ngOnInit() {
    this.recetas$ = this.recetasService.getRecetas();
  }
}