import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RecetasService } from '../../services/recetas.service';
import { Observable, switchMap, of, catchError, tap } from 'rxjs'; // Añadimos catchError y tap
import { Receta } from '../../models/receta';

@Component({
  selector: 'app-receta-detalle',
  standalone: true,
  imports: [CommonModule, AsyncPipe, RouterLink],
  templateUrl: './receta-detalle.component.html',
  styleUrl: './receta-detalle.component.css'
})
export class RecetaDetalleComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private recetasService = inject(RecetasService);
  
  receta$!: Observable<Receta | null>;

  ngOnInit() {
    this.receta$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (id) {
          return this.recetasService.getRecetaById(id).pipe(
            // Esto nos sirve para ver en la consola si el Backend responde bien
            tap(data => console.log('Datos recibidos del Backend:', data)),
            // Si el backend da error (ej: receta no existe), devolvemos null
            catchError(err => {
              console.error('Error al traer la receta del servidor', err);
              return of(null);
            })
          );
        }
        return of(null);
      })
    );
  }

  imgError(event: any) {
    event.target.style.display = 'none';
  }
}