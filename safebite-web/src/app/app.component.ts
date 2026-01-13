import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { RecetasService } from './services/recetas.service';
import { Observable } from 'rxjs';
import { HeaderComponent } from "./components/header/header.component";
import { HeroComponent } from "./components/hero/hero.component";
import { FooterComponent } from "./components/footer/footer.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HeaderComponent, HeroComponent, FooterComponent],
  templateUrl: './app.component.html',  // <-- Ahora apunta al archivo externo
  styleUrl: './app.component.css'       // <-- También para los estilos
})
export class AppComponent {
  private recetasService = inject(RecetasService);
  
  // Esta variable guarda el "chorro" de datos que viene de Firebase
  recetas$: Observable<any[]> = this.recetasService.obtenerRecetas();
}