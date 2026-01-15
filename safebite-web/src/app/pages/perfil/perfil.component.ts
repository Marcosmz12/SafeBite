import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RecetasService } from '../../services/recetas.service';
import { Observable, switchMap, of } from 'rxjs';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { Receta } from '../../models/receta';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RecipeCardComponent],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {
  authService = inject(AuthService);
  recetasService = inject(RecetasService);

  user$ = this.authService.user$;
  misRecetas$!: Observable<Receta[]>;

  ngOnInit() {
    // Esta lógica es avanzada: espera a que haya usuario y entonces busca sus recetas
    this.misRecetas$ = this.user$.pipe(
      switchMap(user => {
        if (user) {
          return this.recetasService.getRecetasPorUsuario(user.uid);
        } else {
          return of([]); // Si no hay usuario, devolvemos lista vacía
        }
      })
    );
  }
}