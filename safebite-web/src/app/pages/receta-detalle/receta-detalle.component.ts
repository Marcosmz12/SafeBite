import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RecetasService } from '../../services/recetas.service';
import { Observable, switchMap, of } from 'rxjs'; 
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
  
  // 1. CORRECCIÓN AQUÍ: Añadimos "| null" para que coincida con el "of(null)"
  receta$!: Observable<Receta | null>;

  ngOnInit() {
    this.receta$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        // Si hay ID busca la receta, si no, emite null
        return id ? this.recetasService.getRecetaById(id) : of(null);
      })
    );
  }

  imgError(event: any) {
    event.target.style.display = 'none';
  }
}