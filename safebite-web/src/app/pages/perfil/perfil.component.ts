import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RecetasService } from '../../services/recetas.service';
import { Observable, switchMap, of } from 'rxjs';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { Receta } from '../../models/receta';
import { PerfilService } from '../../services/perfil.service';
import { AlergiasManagerComponent } from "../../components/alergias-manager/alergias-manager.component";

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RecipeCardComponent, AlergiasManagerComponent],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {
  private authService = inject(AuthService);
  private recetasService = inject(RecetasService);
  private perfilService = inject(PerfilService);

  user$ = this.authService.user$; // Usuario de Firebase
  misRecetas$!: Observable<Receta[]>; // Sus recetas
  listaAlergenos = ['Gluten', 'Lactosa', 'Frutos Secos', 'Huevo', 'Marisco', 'Pescado'];
  alergiasUsuario: string[] = [];

  ngOnInit() {
    // Usamos switchMap para que, en cuanto detecte al usuario, busque sus recetas
    this.misRecetas$ = this.user$.pipe(
      switchMap(user => {
        if (user) {
          return this.recetasService.getRecetasPorAutor(user.uid);
        } else {
          return of([]); // Si no hay usuario, devolvemos lista vacía
        }
      })
    );
    this.user$.subscribe(user => {
      if (user) {
        this.perfilService.getPerfil(user.uid).subscribe(perfil => {
          if (perfil && perfil.alergias) {
            this.alergiasUsuario = perfil.alergias;
          }
        });
      }
    });
  }
  toggleAlergia(alergia: string, uid: string) {
    if (!uid) return;
  
    if (this.alergiasUsuario.includes(alergia)) {
      this.alergiasUsuario = this.alergiasUsuario.filter(a => a !== alergia);
    } else {
      this.alergiasUsuario.push(alergia);
    }
    
    // Guardar en el servicio
    this.perfilService.guardarAlergias(uid, this.alergiasUsuario)
      .then(() => console.log('Preferencia guardada: ', alergia))
      .catch(err => console.error('Error al guardar:', err));
  }
}