import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RecetasService } from '../../services/recetas.service';
import { PerfilService } from '../../services/perfil.service'; // Asegúrate de tener este servicio
import { Observable, switchMap, of } from 'rxjs';
import { Receta } from '../../models/receta';
import { AlergiasManagerComponent } from '../../components/alergias-manager/alergias-manager.component';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';

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

  user$ = this.authService.user$;
  misRecetas$!: Observable<Receta[]>;
  alergiasUsuario: string[] = [];
  listaAlergenos = ['Gluten', 'Lactosa', 'Frutos Secos', 'Huevo'];

  ngOnInit() {
    this.misRecetas$ = this.user$.pipe(
      switchMap(user => user ? this.recetasService.getRecetasPorAutor(user.uid) : of([]))
    );
  }

  // --- AQUÍ ESTABA EL ERROR: Cambiamos .then por .subscribe ---
  toggleAlergia(alergia: string, uid: string) {
    if (this.alergiasUsuario.includes(alergia)) {
      this.alergiasUsuario = this.alergiasUsuario.filter(a => a !== alergia);
    } else {
      this.alergiasUsuario.push(alergia);
    }
    
    // Al usar HttpClient, usamos .subscribe()
    this.perfilService.guardarAlergias(uid, this.alergiasUsuario).subscribe({
      next: () => {
        console.log('Preferencia guardada correctamente');
      },
      error: (err: any) => { // Especificamos 'any' para el error
        console.error('Error al guardar en el servidor:', err);
      }
    });
  }

  obtenerRango(total: number) {
    if (total >= 11) return { nombre: 'Maestro Culinario', clase: 'rango-maestro', icono: '🏆', siguiente: null };
    if (total >= 6)  return { nombre: 'Chef Ejecutivo', clase: 'rango-chef', icono: '👨‍🍳', siguiente: 11 };
    if (total >= 3)  return { nombre: 'Cocinero', clase: 'rango-cocinero', icono: '🍳', siguiente: 6 };
    return { nombre: 'Pinche de Cocina', clase: 'rango-pinche', icono: '🌱', siguiente: 3 };
  }

  calcularProgreso(total: number, siguiente: number | null): number {
    if (!siguiente) return 100;
    return (total / siguiente) * 100;
  }
}