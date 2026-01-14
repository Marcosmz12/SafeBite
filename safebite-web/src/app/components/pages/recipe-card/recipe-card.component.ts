import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router'; // <--- 1. Importa esto
import { Receta } from '../../../models/receta';

@Component({
  selector: 'app-recipe-card',
  standalone: true,
  imports: [RouterLink], // <--- 2. Añádelo aquí
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.css'
})
export class RecipeCardComponent {
  @Input() receta!: Receta;
}