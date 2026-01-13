import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common'; // Necesario para el pipe async y ngIf
import { RecetasService } from '../../services/recetas.service'; // Ajusta la ruta a tu servicio
import { Observable } from 'rxjs';
import { Receta } from '../../models/receta'; // Tu interfaz

@Component({
  selector: 'app-recetas',
  standalone: true,
  imports: [CommonModule], // Importante para que funcione el | async
  templateUrl: './recetas.component.html',
  styleUrl: './recetas.component.css'
})
export class RecetasComponent {
  // 1. Inyectamos el servicio
  private recetasService = inject(RecetasService);

  // 2. Creamos un "Observable" que contendrá la lista de recetas
  recetas$: Observable<Receta[]>;

  // 3. Definimos las categorías exactamente como las tienes en Firebase
  categorias = ['Entrantes', 'Platos Principales', 'Postres'];

  constructor() {
    // 4. Al arrancar, pedimos las recetas al servicio
    this.recetas$ = this.recetasService.getRecetas();
  }
}