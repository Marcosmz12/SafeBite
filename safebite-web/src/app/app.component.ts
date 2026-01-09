import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RecetasService } from './services/recetas.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, AsyncPipe],
  templateUrl: './app.component.html',  // <-- Ahora apunta al archivo externo
  styleUrl: './app.component.css'       // <-- También para los estilos
})
export class AppComponent {
  private recetasService = inject(RecetasService);
  
  // Esta variable guarda el "chorro" de datos que viene de Firebase
  recetas$: Observable<any[]> = this.recetasService.obtenerRecetas();
}